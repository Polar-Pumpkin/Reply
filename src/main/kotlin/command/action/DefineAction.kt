package me.parrot.mirai.command.action

import me.parrot.mirai.data.trigger.ReplyTrigger
import me.parrot.mirai.function.reply
import me.parrot.mirai.manager.Actions
import me.parrot.mirai.manager.Caches
import me.parrot.mirai.registry.Triggers
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.content

/**
 * Reply
 * me.parrot.mirai.command.action.DefineAction
 *
 * @author legoshi
 * @version 1
 * @since 2023/11/01 10:54
 */
class DefineAction(val trigger: ReplyTrigger<*>?) : CommandAction {

    override suspend fun initialize(sender: UserCommandSender) {
        sender.reply { +"请发送触发器定义(一行一个)" }
    }

    override suspend fun run(event: MessageEvent) {
        if (!Caches.excludeResponses(event)) {
            return
        }
        val trigger = with(event) { Triggers.parse(event.message.content) }
        Actions.schedule(event, CreateAction(trigger))
    }

}