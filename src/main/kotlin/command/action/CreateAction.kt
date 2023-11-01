package me.parrot.mirai.command.action

import me.parrot.mirai.data.content.Content
import me.parrot.mirai.data.model.Response
import me.parrot.mirai.data.trigger.ReplyTrigger
import me.parrot.mirai.function.reply
import me.parrot.mirai.manager.Actions
import me.parrot.mirai.registry.Triggers
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Message
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Reply
 * me.parrot.mirai.command.action.CreateAction
 *
 * @author legoshi
 * @version 1
 * @since 2023/11/01 10:39
 */
class CreateAction(val trigger: ReplyTrigger<out Message>?) : CommandAction {

    override suspend fun initialize(sender: UserCommandSender) {
        sender.reply { +"请发送触发器" }
    }

    override suspend fun initialize(event: MessageEvent) {
        event.reply {
            +"已解析触发器, 请发送自动回复内容:\n"
            +trigger.toString()
        }
    }

    override suspend fun run(event: MessageEvent) {
        if (trigger == null) createTrigger(event) else createContent(event)
    }

    private suspend fun createTrigger(event: MessageEvent) {
        if (!Response.isExclusive(event)) {
            return
        }
        val trigger = with(event) { Triggers.createDefault(message, message) }
        Actions.schedule(event, CreateAction(trigger))
    }

    private suspend fun createContent(event: MessageEvent) {
        trigger ?: return
        val content = Content.wrap(event.message, event.sender)
        val response = transaction {
            Response.new {
                this.trigger = this@CreateAction.trigger
                this.content = content
                this.creator = event.sender.id
            }
        }
        event.reply { +"已创建自动回复 #${response.id.value}" }
    }

}