package me.parrot.data.action

import me.parrot.Reply
import me.parrot.data.context.ChainContext
import me.parrot.data.trigger.ReplyTrigger
import me.parrot.function.replyMessage
import me.parrot.storage.ReplyDefines
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.messageChainOf

/**
 * Reply
 * me.parrot.data.action.EditAction
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 23:39
 */
data class EditAction(val trigger: ReplyTrigger?, val force: Boolean) : ScheduledAction {

    override suspend fun execute(event: MessageEvent) {
        if (trigger == null) event.createTrigger() else event.createContext()
    }

    private suspend fun MessageEvent.createTrigger() {
        val (triggerId, trigger) = ReplyDefines.match(message) ?: return subject.replyMessage(message, "未匹配到触发器")
        val context = ReplyDefines.getContext(triggerId)
        subject.replyMessage(message) {
            +"旧内容如下, 请发送新的内容:\n"
            +(context?.show(subject) ?: messageChainOf(PlainText("(无效)")))
        }
        Reply.schedule(sender.id, EditAction(trigger, force))
    }

    private suspend fun MessageEvent.createContext() {
        checkNotNull(trigger)
        val chain = ChainContext.of(message, force)
        if (chain.isNullOrEmpty()) {
            return subject.replyMessage(message, "无法解析此内容")
        }
        ReplyDefines.upload(trigger, if (chain.size == 1) chain.first() else chain, force)
        subject.replyMessage(message, "已保存自动回复")
    }

}
