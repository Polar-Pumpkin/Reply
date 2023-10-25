package me.parrot.command

import me.parrot.Reply
import me.parrot.data.action.CreateAction
import me.parrot.data.action.EditAction
import me.parrot.storage.ReplyContexts
import me.parrot.storage.ReplyDefines
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.UserCommandSender

/**
 * Reply
 * me.parrot.command.CommandHandler
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 15:05
 */
object ReplyCommand : CompositeCommand(Reply, "reply") {

    @SubCommand
    suspend fun CommandSender.reload() {
        ReplyContexts.build()
        ReplyDefines.build()
        sendMessage("重新加载完成, 共加载 ${ReplyDefines.size} 条自动回复 (${ReplyContexts.size} 项内容)")
    }

    @SubCommand
    suspend fun UserCommandSender.create() {
        Reply.schedule(user.id, CreateAction(null))
        sendMessage("请发送自动回复触发器")
    }

    @SubCommand
    suspend fun UserCommandSender.edit() {
        Reply.schedule(user.id, EditAction(null))
        sendMessage("请发送自动回复触发器")
    }

    @SubCommand
    suspend fun UserCommandSender.cancel() {
        sendMessage(if (Reply.cancel(user.id)) "编辑已取消" else "没有正在编辑的自动回复")
    }

}