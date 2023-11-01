package me.parrot.mirai.data.trigger

import me.parrot.mirai.data.Demand
import me.parrot.mirai.internal.function.createDefaultOptions
import me.parrot.mirai.internal.function.parseOptions
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.MessageChain

/**
 * Reply
 * me.parrot.mirai.data.trigger.FaceTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 15:37
 */
data class FaceTrigger(val faceId: Int) : ReplyTrigger<Face>() {

    override suspend fun test(message: Face): Boolean {
        return message.id == faceId
    }

    companion object : ReplyTriggerParser<Face> {

        override val uniqueId: String = "face"

        override val arguments: Map<String, List<String>> = mapOf(
            "faceId" to listOf("黄豆表情 ID")
        )

        context(MessageEvent)
        override suspend fun parse(demand: Demand): FaceTrigger {
            val (faceId) = demand.positions
            return FaceTrigger(faceId.toInt())
                .parseOptions(demand)
        }

        context(MessageEvent)
        override suspend fun createDefault(message: Face, origin: MessageChain): FaceTrigger {
            return FaceTrigger(message.id)
                .createDefaultOptions(message, origin)
        }

    }

}
