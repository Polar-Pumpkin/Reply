package me.parrot.mirai.command.action

import me.parrot.mirai.data.content.Content
import me.parrot.mirai.data.trigger.ReplyTrigger
import me.parrot.mirai.function.reply
import me.parrot.mirai.manager.Actions
import me.parrot.mirai.manager.Caches
import me.parrot.mirai.registry.Triggers
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.event.events.MessageEvent

/**
 * Reply
 * me.parrot.mirai.command.action.CreateAction
 *
 * @author legoshi
 * @version 1
 * @since 2023/11/01 10:39
 */
class CreateAction(val trigger: ReplyTrigger<*>?) : CommandAction {

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
        if (!Caches.excludeResponses(event)) {
            return
        }
        val trigger = with(event) { Triggers.createDefault(message, message) }
        Actions.schedule(event, CreateAction(trigger))
    }

    private suspend fun createContent(event: MessageEvent) {
        trigger ?: return
        val content = Content.wrap(event.message, event.sender)
        val response = Caches.newResponse(event.sender.id, trigger, content)
        event.reply { +"已创建自动回复 #${response.id.value}" }
    }

}