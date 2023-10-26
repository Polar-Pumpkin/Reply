package me.parrot.data.action

import me.parrot.function.replyMessage
import me.parrot.storage.ReplyDefines
import net.mamoe.mirai.event.events.MessageEvent

/**
 * Reply
 * me.parrot.data.action.DeleteAction
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/26 09:37
 */
class DeleteAction : ScheduledAction {

    override suspend fun execute(event: MessageEvent) {
        with(event) {
            val (triggerId, _) = ReplyDefines.match(message)
                ?: return subject.replyMessage(message, "未匹配到触发器")
            subject.replyMessage(message, if (ReplyDefines.delete(triggerId)) "已删除" else "删除失败")
        }
    }

}
