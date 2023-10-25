package me.parrot.data.action

import me.parrot.Reply
import me.parrot.data.context.ChainContext
import me.parrot.data.trigger.ReplyTrigger
import me.parrot.function.replyMessage
import me.parrot.storage.ReplyDefines
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*

/**
 * Reply
 * me.parrot.data.action.CreateAction
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 21:55
 */
data class CreateAction(val trigger: ReplyTrigger?) : ScheduledAction {

    override suspend fun execute(event: MessageEvent) {
        if (trigger == null) event.createTrigger() else event.createContext()
    }

    private suspend fun MessageEvent.createTrigger() {
        ReplyDefines.match(message)?.let { (triggerId, _) ->
            val context = ReplyDefines.getContext(triggerId)
            subject.replyMessage(message) {
                +"已存在自动回复条目:\n"
                +(context?.show(subject) ?: messageChainOf(PlainText("(无效)")))
            }
            return
        }

        val trigger = message.filterIsInstance<MessageContent>()
            .filter { it !is UnsupportedMessage }
            .takeIf { it.size == 1 }
            ?.first()
            ?.let {
                when (it) {
                    is PlainText -> ReplyTrigger.wrap(it.content)
                    is Image -> ReplyTrigger.wrap(it)
                    else -> null
                }
            }
            ?: return subject.replyMessage(message, "无法解析成触发器")
        subject.replyMessage(message, "请发送自动回复内容")
        Reply.schedule(sender.id, CreateAction(trigger))
    }

    private suspend fun MessageEvent.createContext() {
        checkNotNull(trigger)
        val chain = ChainContext.of(message)
        if (chain.isNullOrEmpty()) {
            return subject.replyMessage(message, "无法解析此内容")
        }
        ReplyDefines.upload(trigger, if (chain.size == 1) chain.first() else chain)
        subject.replyMessage(message, "已保存自动回复")
    }

}
