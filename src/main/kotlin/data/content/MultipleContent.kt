package me.parrot.mirai.data.content

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChainBuilder

/**
 * Reply
 * me.parrot.mirai.data.content.MultipleContent
 *
 * @author legoshi
 * @version 1
 * @since 2023/11/01 11:28
 */
data class MultipleContent(val contents: MutableSet<Content>) : Content {

    context(MessageChainBuilder)
    override suspend fun append(origin: MessageEvent?) {
        contents.random().append(origin)
    }

}