package me.parrot.mirai.data.trigger

import kotlinx.serialization.Serializable
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
@Serializable
data class FaceTrigger(val faceId: Int) : ReplyTrigger<Face>() {

    override val clazz: Class<Face>
        get() = Face::class.java

    override suspend fun test(message: Face): Boolean {
        return message.id == faceId
    }

    companion object : ReplyTriggerParser<Face, FaceTrigger> {

        override val uniqueId: String = "face"

        override val clazz: Class<FaceTrigger> = FaceTrigger::class.java

        override val arguments: Map<String, List<String>> = mapOf(
            "faceId" to listOf("黄豆表情 ID")
        )

        context(MessageEvent)
        override suspend fun parse(demand: Demand): FaceTrigger {
            val faceId = demand.argument("faceId", 0)
            return FaceTrigger(faceId.toInt())
                .parseOptions(demand)
        }

        context(MessageEvent)
        override suspend fun createDefault(message: Face, origin: MessageChain?): FaceTrigger {
            return FaceTrigger(message.id)
                .createDefaultOptions(origin)
        }

    }

}
