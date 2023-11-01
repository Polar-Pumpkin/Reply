package me.parrot.mirai.command

import me.parrot.mirai.Reply
import me.parrot.mirai.command.action.AppendAction
import me.parrot.mirai.command.action.CreateAction
import me.parrot.mirai.command.action.DefineAction
import me.parrot.mirai.command.action.EditAction
import me.parrot.mirai.data.model.Link
import me.parrot.mirai.function.maxPage
import me.parrot.mirai.function.page
import me.parrot.mirai.function.reply
import me.parrot.mirai.manager.Actions
import me.parrot.mirai.manager.Caches
import me.parrot.mirai.manager.Histories
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.contact.User

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
    @SubCommand
    suspend fun CommandSender.reload() {
        Caches.build()
        reply { +"重载完成" }
    }

    // list
    @SubCommand
    suspend fun CommandSender.list(page: Int) {
        val size = 10L
        val pages = Caches.getResponses().values.maxPage(size)
        if (page > pages) {
            reply { +"未找到第 $page 页, 共 $pages 页" }
            return
        }
        reply {
            +"第 $page 页 / 共 $pages 页\n"
            Caches.getResponses().values.page(page, size) {
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
        val response = Caches.getResponseOrNull(responseId) ?: return reply { +"未找到自动回复 #$responseId" }
        reply { response.append(fromEvent) }
    }

    // last
    @SubCommand
    suspend fun CommandSenderOnMessage<*>.last(user: User = fromEvent.sender) {
        val history = Histories[user.id] ?: return reply { +"未找到最近一次自动回复" }
        reply {
            history.responses.forEach { response ->
                response.append(fromEvent)
                +"- - - - -\n"
            }
        }
    }

    // edit
    @SubCommand
    suspend fun UserCommandSender.edit(responseId: Long) {
        val response = Caches.getResponseOrNull(responseId) ?: return reply { +"未找到自动回复 #$responseId" }
        Actions.schedule(this, EditAction(response))
    }

    // append
    @SubCommand
    suspend fun UserCommandSender.append(responseId: Long) {
        val response = Caches.getResponseOrNull(responseId) ?: return reply { +"未找到自动回复 #$responseId" }
        Actions.schedule(this, AppendAction(response))
    }

    // cancel
    @SubCommand
    suspend fun UserCommandSender.cancel() =
        reply { if (Actions.cancel(user.id)) +"操作已取消" else +"没有正在进行中的操作" }

    // delete
    @SubCommand
    suspend fun CommandSender.delete(responseId: Long) {
        val response = Caches.getResponseOrNull(responseId) ?: return reply { +"未找到自动回复 #$responseId" }
        Caches.deleteResponse(response)
        reply { +"已删除自动回复 #$responseId" }
    }

    // link
    @SubCommand
    suspend fun CommandSender.link(action: String, argument: String) {
        when (action) {
            "create" -> {
                val (activeId, direction, passiveId) = Link.solve(argument)
                    ?: return reply { +"无法解析连接: $argument" }
                val active = Caches.getResponseOrNull(activeId) ?: return reply { +"未找到自动回复 #$activeId" }
                val passive = Caches.getResponseOrNull(passiveId) ?: return reply { +"未找到自动回复 #$passiveId" }
                Caches.findLink(activeId, passiveId)?.let {
                    return reply { +"两者之间已经存在连接: ${it.direction.connection}" }
                }
                Caches.newLink(active, passive, direction)
                reply { +"已创建连接: #$activeId ${direction.connection} #$passiveId" }
            }

            "delete" -> {
                val (activeId, direction, passiveId) = Link.solve(argument)
                    ?: return reply { +"无法解析连接: $argument" }
                val link = Caches.findLink(activeId, passiveId, direction)
                    ?: return reply { +"未找到对应的连接" }
                Caches.deleteLink(link)
                reply { +"已删除连接" }
            }

            "list" -> {
                val page = argument.toIntOrNull() ?: return reply { +"无效的页码, 请使用正整数 : $argument" }
                val size = 10L
                val pages = Caches.getLinks().maxPage(size)
                if (page > pages) {
                    reply { +"未找到第 $page 页, 共 $pages 页" }
                    return
                }
                reply {
                    +"第 $page 页, 共 $pages 页"
                    Caches.getLinks().page(page, size) {
                        +"#${it.active.id.value} ${it.direction.connection} #${it.passive.id.value}"
                    }
                }
            }

            else -> reply { +"未知的操作: $action" }
        }
    }

}