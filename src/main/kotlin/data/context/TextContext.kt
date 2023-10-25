package me.parrot.data.context

import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.toMessageChain

/**
 * Reply
 * me.parrot.data.context.TextContext
 * 单行纯文本内容
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 12:13
 */
@Serializable
data class TextContext(val text: String) : ReplyContext {

    override suspend fun build(contact: Contact): MessageChain {
        return PlainText(text).toMessageChain()
    }

    override fun match(context: ReplyContext): Boolean {
        return context is TextContext && context.text.trim() == text
    }

}
