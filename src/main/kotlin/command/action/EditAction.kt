package me.parrot.mirai.command.action

import me.parrot.mirai.data.content.Content
import me.parrot.mirai.data.model.Response
import me.parrot.mirai.function.reply
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.event.events.MessageEvent
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Reply
 * me.parrot.mirai.command.action.EditAction
 *
 * @author legoshi
 * @version 1
 * @since 2023/11/01 13:33
 */
class EditAction(val response: Response) : CommandAction {

    override suspend fun initialize(sender: UserCommandSender) {
        sender.reply { +"请发送自动回复内容" }
    }

    override suspend fun run(event: MessageEvent) {
        val content = Content.wrap(event.message, event.sender)
        transaction {
            response.content = content
            response.flush()
        }
        event.reply { +"已修改自动回复 #${response.id.value} 的内容" }
    }

}