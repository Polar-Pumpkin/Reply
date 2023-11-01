package me.parrot.mirai.data.content

import kotlinx.serialization.Serializable
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.MessageChainBuilder

/**
 * Reply
 * me.parrot.mirai.data.content.FaceContent
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 15:57
 */
@Serializable
data class FaceContent(val faceId: Int) : Content {

    context(MessageChainBuilder)
    override suspend fun append(origin: MessageEvent?) {
        +Face(faceId)
    }

}
