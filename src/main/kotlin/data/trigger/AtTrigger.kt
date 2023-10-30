package me.parrot.mirai.data.trigger

import me.parrot.mirai.data.Demand
import me.parrot.mirai.internal.function.createDefaultOptions
import me.parrot.mirai.internal.function.parseOptions
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChain

/**
 * Reply
 * me.parrot.mirai.data.trigger.AtTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 13:00
 */
data class AtTrigger(val userId: Long) : ReplyTrigger<At>() {

    override suspend fun test(message: At): Boolean = message.target == userId

    companion object : ReplyTriggerParser<At> {

        override val uniqueId: String = "at"

        override val arguments: Map<String, List<String>> = mapOf(
            "userId" to listOf("被提及用户 ID")
        )

        context(MessageEvent)
        override suspend fun parse(demand: Demand): AtTrigger {
            val (userId) = demand.positions
            return AtTrigger(userId.toLong())
                .parseOptions(demand)
        }

        context(MessageEvent)
        override suspend fun createDefault(message: At, origin: MessageChain): AtTrigger {
            return AtTrigger(message.target)
                .createDefaultOptions(message, origin)
        }

    }

}