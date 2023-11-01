package me.parrot.mirai.data.content

import kotlinx.serialization.Serializable
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.MessageSource.Key.quote

/**
 * Reply
 * me.parrot.mirai.data.content.QuoteContent
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 15:55
 */
@Serializable
object QuoteContent : Content {

    context(MessageChainBuilder)
    override suspend fun append(origin: MessageEvent?) {
        origin ?: return
        +origin.message.quote()
    }

}