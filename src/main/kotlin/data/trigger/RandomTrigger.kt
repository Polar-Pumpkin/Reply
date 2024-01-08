package me.parrot.mirai.data.trigger

import me.parrot.mirai.data.Demand
import me.parrot.mirai.internal.function.parseOptions
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageSource
import java.util.concurrent.ThreadLocalRandom

/**
 * Reply
 * me.parrot.mirai.data.trigger.RandomTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2024/01/08 16:10
 */
data class RandomTrigger(val rate: Double) : ReplyTrigger<MessageSource>() {

    override val isVirtual: Boolean
        get() = true

    override val clazz: Class<MessageSource>
        get() = TODO("Not yet implemented")

    override suspend fun test(message: MessageSource): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun test(event: MessageEvent): Boolean {
        return ThreadLocalRandom.current().nextDouble() <= rate
    }

    companion object : ReplyTriggerParser<MessageSource, RandomTrigger> {

        override val uniqueId: String = "rate"

        override val clazz: Class<RandomTrigger> = RandomTrigger::class.java

        override val arguments: Map<String, List<String>> = mapOf(
            "rate" to listOf("触发概率 (0.0 ~ 1.0)")
        )

        context(MessageEvent)
        override suspend fun parse(demand: Demand): RandomTrigger {
            val rate = demand.argument("rate", 0)
            return RandomTrigger(rate.toDouble())
                .parseOptions(demand)
        }

        context(MessageEvent)
        override suspend fun createDefault(message: MessageSource, origin: MessageChain?): RandomTrigger {
            TODO("Not yet implemented")
        }

    }

}
