package me.parrot.command

import me.parrot.Reply
import me.parrot.data.trigger.ReplyTrigger
import me.parrot.storage.ReplyContexts
import me.parrot.storage.ReplyDefines
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.toMessageChain

/**
 * Reply
 * me.parrot.command.CommandHandler
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 15:05
 */
object CommandHandler : CompositeCommand(Reply, "reply") {

    @SubCommand
    suspend fun UserCommandSender.create(text: String) {
        if (existence(PlainText(text).toMessageChain())) return
        val trigger = ReplyTrigger.wrap(text)
        if (trigger == null) {
            sendMessage("无法解析该文本内容为触发器")
            return
        }
        Reply.schedule(user.id, trigger)
        sendMessage("请发送自动回复内容")
    }

    // @SubCommand
    suspend fun UserCommandSender.create(image: Image) {
        if (existence(image.toMessageChain())) return
        val trigger = ReplyTrigger.wrap(image)
        Reply.schedule(user.id, trigger)
        sendMessage("请发送自动回复内容")
    }

    @SubCommand
    suspend fun UserCommandSender.cancel() {
        if (Reply.cancel(user.id)) {
            sendMessage("已取消编辑自动回复")
        } else {
            sendMessage("没有正在编辑的自动回复")
        }
    }

    private suspend fun UserCommandSender.existence(message: MessageChain): Boolean {
        val (triggerId, _) = ReplyDefines.match(message) ?: return false
        val context = ReplyDefines.getContextId(triggerId)?.let(ReplyContexts::get)
        if (context != null) {
            sendMessage(PlainText("该触发器已设置自动回复:\n") + context.build(subject))
        } else {
            // 请使用编辑功能修改回复内容
            sendMessage("该触发器已设置自动回复, 但是回复内容已失效")
        }
        return true
    }

}