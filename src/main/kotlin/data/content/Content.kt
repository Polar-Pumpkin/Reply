package me.parrot.mirai.data.content

import kotlinx.serialization.Serializable
import me.parrot.mirai.data.binary.ImageMetadata
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*

/**
 * Reply
 * me.parrot.mirai.data.content.Content
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/27 15:09
 */
@Serializable
sealed interface Content {

    context(MessageChainBuilder)
    suspend fun append(origin: MessageEvent? = null)

    companion object {

        suspend fun wrap(message: Message, sender: User? = null, bot: Bot? = sender?.bot): Content {
            return when (message) {
                is At -> {
                    if (sender != null && message.target == sender.id) {
                        return AtContent
                    }
                    if (bot != null && message.target == bot.id) {
                        return QuoteContent
                    }
                    throw IllegalArgumentException("Unsupported @ content")
                }

                is Face -> FaceContent(message.id)
                is Image -> ImageContent((ImageMetadata.find(message) ?: ImageMetadata.upload(message)).id.value)
                is PlainText -> TextContent(message.content)
                is MessageChain -> ChainContent(
                    message
                        .filterIsInstance<MessageContent>()
                        .filter { it !is UnsupportedMessage }
                        .map { wrap(it, sender, bot) }
                )

                else -> throw IllegalArgumentException("Unsupported message type: ${message::class.java.canonicalName}")
            }
        }

    }

}