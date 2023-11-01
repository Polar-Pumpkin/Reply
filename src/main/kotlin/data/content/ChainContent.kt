package me.parrot.mirai.data.content

import kotlinx.serialization.Serializable
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChainBuilder

/**
 * Reply
 * me.parrot.mirai.data.content.ChainContent
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 16:04
 */
@Serializable
data class ChainContent(val content: List<Content>) : Content {

    context(MessageChainBuilder)
    override suspend fun append(origin: MessageEvent?) {
        content.forEach {
            it.append(origin)
        }
    }

}
