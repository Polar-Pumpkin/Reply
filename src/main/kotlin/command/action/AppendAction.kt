package me.parrot.mirai.command.action

import me.parrot.mirai.Reply
import me.parrot.mirai.data.content.Content
import me.parrot.mirai.data.content.MultipleContent
import me.parrot.mirai.data.model.Response
import me.parrot.mirai.function.reply
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.event.events.MessageEvent
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Reply
 * me.parrot.mirai.command.action.AppendAction
 *
 * @author legoshi
 * @version 1
 * @since 2023/11/01 13:26
 */
class AppendAction(val response: Response) : CommandAction {

    override suspend fun initialize(sender: UserCommandSender) {
        sender.reply { +"请发送自动回复内容" }
    }

    override suspend fun run(event: MessageEvent) {
        val content = response.content
        val created = Content.wrap(event.message, event.sender)
        transaction(Reply.db) {
            response.content = if (content is MultipleContent) {
                content.apply {
                    contents += created
                }
            } else {
                MultipleContent(mutableSetOf(content, created))
            }
            response.flush()
        }
        event.reply { +"已追加新内容至自动回复 #${response.id.value}" }
    }

}