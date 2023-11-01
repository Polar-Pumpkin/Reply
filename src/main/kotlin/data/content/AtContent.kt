package me.parrot.mirai.data.content

import kotlinx.serialization.Serializable
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChainBuilder

/**
 * Reply
 * me.parrot.mirai.data.content.AtContent
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 15:56
 */
@Serializable
object AtContent : Content {

    context(MessageChainBuilder)
    override suspend fun append(origin: MessageEvent?) {
        origin ?: return
        +At(origin.sender)
    }

}