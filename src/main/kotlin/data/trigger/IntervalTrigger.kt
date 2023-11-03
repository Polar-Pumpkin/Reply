package me.parrot.mirai.data.trigger

import kotlinx.serialization.Serializable
import me.parrot.mirai.data.Demand
import me.parrot.mirai.storage.Intervals
import net.mamoe.mirai.event.events.GroupAwareMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageSource

/**
 * Reply
 * me.parrot.mirai.data.trigger.IntervalTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 14:56
 */
@Serializable
data class IntervalTrigger(val days: Int) : ReplyTrigger<MessageSource>() {

    override val clazz: Class<MessageSource>
        get() = TODO("Not yet implemented")

    override suspend fun test(message: MessageSource): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun test(event: MessageEvent): Boolean {
        if (event !is GroupAwareMessageEvent) {
            return false
        }
        val duration = Intervals.days(event)
        return duration.inWholeDays >= days
    }

    companion object : ReplyTriggerParser<MessageSource, IntervalTrigger> {

        override val uniqueId: String = "interval"

        override val clazz: Class<IntervalTrigger> = IntervalTrigger::class.java

        override val arguments: Map<String, List<String>> = mapOf(
            "days" to listOf("发言间隔的天数")
        )

        context(MessageEvent)
        override suspend fun parse(demand: Demand): IntervalTrigger {
            val days = demand.argument("days", 0)
            return IntervalTrigger(days.toInt())
        }

        context(MessageEvent)
        override suspend fun createDefault(message: MessageSource, origin: MessageChain?): IntervalTrigger {
            TODO("Not yet implemented")
        }

    }

}
