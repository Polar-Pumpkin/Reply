package me.parrot.mirai.command

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.parrot.mirai.Reply
import me.parrot.mirai.command.action.AppendAction
import me.parrot.mirai.command.action.CreateAction
import me.parrot.mirai.command.action.DefineAction
import me.parrot.mirai.command.action.EditAction
import me.parrot.mirai.data.model.Link
import me.parrot.mirai.data.model.Response
import me.parrot.mirai.function.reply
import me.parrot.mirai.internal.function.onPage
import me.parrot.mirai.manager.Actions
import me.parrot.mirai.manager.Histories
import me.parrot.mirai.registry.TriggerOptions
import me.parrot.mirai.registry.Triggers
import me.parrot.mirai.storage.Links
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.contact.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * Reply
 * me.parrot.mirai.command.ReplyCommand
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 16:25
 */
object ReplyCommand : CompositeCommand(Reply, "reply") {

    // reload

    // help
    @SubCommand
    suspend fun CommandSender.help(name: String? = null) {
        if (name == null) {
            reply {
                +"已注册的触发器解析器:\n"
                +Triggers.keys.joinToString(", ")
            }
        } else {
            val parser = Triggers[name] ?: return reply { +"未知的触发器解析器: $name" }
            val options = TriggerOptions.target(parser.clazz)
            reply {
                +"触发器解析器: ${parser.uniqueId}\n"
                if (parser.arguments.isNotEmpty()) {
                    +"位置参数:\n"
                    parser.arguments.forEach { (name, description) ->
                        +"$name:\n"
                        +description.ifEmpty { listOf("(无介绍)") }.joinToString { "$it\n" }
                    }
                }
                if (options.isNotEmpty()) {
                    +"触发器选项:\n"
                    options.forEach { option ->
                        +"${option.uniqueId}:\n"
                        +option.description.ifEmpty { listOf("(无介绍)") }.joinToString { "$it\n" }
                    }
                }
            }
        }
    }

    // list
    @SubCommand
    suspend fun CommandSender.list(page: Int, size: Long = 10) {
        newSuspendedTransaction {
            Response.all().toList().onPage(page, size) {
                +"#${it.id.value} ${it.trigger}\n"
            }
        }
    }

    // create
    @SubCommand
    suspend fun UserCommandSender.create() = Actions.schedule(this, CreateAction(null))

    // define
    @SubCommand
    suspend fun UserCommandSender.define() = Actions.schedule(this, DefineAction(null))

    // info
    @SubCommand
    suspend fun CommandSenderOnMessage<*>.info(responseId: Long) {
        newSuspendedTransaction {
            response(responseId)?.let {
                reply { it.append(fromEvent) }
            }
        }
    }

    // last
    @SubCommand
    suspend fun CommandSenderOnMessage<*>.last(user: User = fromEvent.sender) {
        val history = Histories[user.id] ?: return reply { +"未找到最近一次自动回复" }
        newSuspendedTransaction {
            reply {
                history.responses.forEach { response ->
                    response.append(fromEvent)
                    +"- - - - -\n"
                }
            }
        }
    }

    // edit
    @SubCommand
    suspend fun UserCommandSender.edit(responseId: Long) {
        newSuspendedTransaction {
            response(responseId)?.let {
                Actions.schedule(this@edit, EditAction(it))
            }
        }
    }

    // append
    @SubCommand
    suspend fun UserCommandSender.append(responseId: Long) {
        newSuspendedTransaction {
            response(responseId)?.let {
                Actions.schedule(this@append, AppendAction(it))
            }
        }
    }

    // cancel
    @SubCommand
    suspend fun UserCommandSender.cancel() =
        reply { if (Actions.cancel(user.id)) +"操作已取消" else +"没有正在进行中的操作" }

    // delete
    @SubCommand
    suspend fun CommandSender.delete(responseId: Long) {
        newSuspendedTransaction {
            response(responseId)?.let {
                it.deleted = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                it.flush()
                reply { +"已删除自动回复 #$responseId" }
            }
        }
    }

    // link
    @SubCommand
    suspend fun CommandSender.link(action: String, argument: String) {
        when (action) {
            "create" -> {
                val (activeId, direction, passiveId) = Link.solve(argument)
                    ?: return reply { +"无法解析连接: $argument" }
                newSuspendedTransaction {
                    Link.find { (Links.active eq activeId) and (Links.passive eq passiveId) }
                        .firstOrNull()
                        ?.let {
                            reply { +"两者之间已经存在连接: ${it.direction.connection}" }
                            return@newSuspendedTransaction
                        }
                    val responseA = response(activeId) ?: return@newSuspendedTransaction
                    val responseP = response(passiveId) ?: return@newSuspendedTransaction
                    Link.new {
                        this.active = responseA
                        this.passive = responseP
                        this.direction = direction
                    }
                    reply { +"已创建连接: #$activeId ${direction.connection} #$passiveId" }
                }
            }

            "delete" -> {
                val (activeId, direction, passiveId) = Link.solve(argument)
                    ?: return reply { +"无法解析连接: $argument" }
                newSuspendedTransaction {
                    val link = Link.find {
                        (Links.active eq activeId) and (Links.passive eq passiveId) and (Links.direction eq direction)
                    }.firstOrNull() ?: return@newSuspendedTransaction reply { +"未找到对应的连接" }
                    link.delete()
                    reply { +"已删除连接" }
                }
            }

            "list" -> {
                val page = argument.toIntOrNull() ?: return reply { +"无效的页码($argument), 请使用正整数" }
                newSuspendedTransaction {
                    Link.all().toList().onPage(page) {
                        +"#${it.active.id.value} ${it.direction.connection} #${it.passive.id.value}"
                    }
                }
            }

            else -> reply { +"未知的操作: $action" }
        }
    }

    private suspend fun CommandSender.response(responseId: Long): Response? {
        val response = Response.findById(responseId)
        if (response == null) {
            reply { +"未找到自动回复 #$responseId" }
            return null
        }
        return response
    }

}