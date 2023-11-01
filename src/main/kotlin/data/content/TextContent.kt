package me.parrot.mirai.data.content

import kotlinx.serialization.Serializable
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChainBuilder

/**
 * Reply
 * me.parrot.mirai.data.content.TextContent
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 15:49
 */
@Serializable
data class TextContent(val text: String) : Content {

    context(MessageChainBuilder)
    override suspend fun append(origin: MessageEvent?) {
        +text
    }

}
