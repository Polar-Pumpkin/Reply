package me.parrot.data.context

import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.messageChainOf

/**
 * Reply
 * me.parrot.data.context.ReplyContext
 * 自动回复内容
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 09:37
 */
@Serializable
sealed interface ReplyContext {
    suspend fun build(contact: Contact): MessageChain
    fun match(context: ReplyContext): Boolean

    suspend fun show(contact: Contact): MessageChain {
        return try {
            build(contact)
        } catch (ex: Throwable) {
            ex.printStackTrace()
            messageChainOf(PlainText("(错误)"))
        }
    }
}