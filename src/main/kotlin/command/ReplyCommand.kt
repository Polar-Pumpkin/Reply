package me.parrot.command

import me.parrot.Reply
import me.parrot.data.action.CreateAction
import me.parrot.data.action.DeleteAction
import me.parrot.data.action.EditAction
import me.parrot.storage.ReplyContexts
import me.parrot.storage.ReplyDefines
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.message.data.buildMessageChain
import kotlin.math.roundToInt

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
    suspend fun UserCommandSender.list(page: Int = 1) {
        val size = 10
        val amount = ReplyDefines.size
        val total = (amount / size.toDouble()).roundToInt()
        if (page <= total) {
            val triggers = ReplyDefines.getPage(page, size)
            sendMessage(buildMessageChain {
                +"第 $page 页 / 共 $total 页:\n"
                triggers.forEach {
                    +"${it.text}\n"
                }
            })
        } else {
            sendMessage("第 $page 页不存在, 共 $total 页")
        }
    }

    @SubCommand
    suspend fun UserCommandSender.create() {
        Reply.schedule(user.id, CreateAction(null))
        sendMessage("请发送自动回复触发器")
    }

    @SubCommand
    suspend fun UserCommandSender.edit(force: Boolean = false) {
        Reply.schedule(user.id, EditAction(null, force))
        sendMessage("请发送自动回复触发器")
    }

    @SubCommand
    suspend fun UserCommandSender.delete() {
        Reply.schedule(user.id, DeleteAction())
        sendMessage("请发送自动回复触发器")
    }

    @SubCommand
    suspend fun UserCommandSender.cancel() {
        sendMessage(if (Reply.cancel(user.id)) "编辑已取消" else "没有正在编辑的自动回复")
    }

}