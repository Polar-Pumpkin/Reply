package me.parrot.mirai.data.trigger

import me.parrot.mirai.data.Demand
import me.parrot.mirai.registry.Triggers
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageContent
import net.mamoe.mirai.message.data.UnsupportedMessage

/**
 * Reply
 * me.parrot.mirai.data.trigger.ChainTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 15:19
 */
data class ChainTrigger(val triggers: List<ReplyTrigger<*>>) : ReplyTrigger<MessageChain>() {

    override suspend fun test(message: MessageChain): Boolean {
        return triggers.all { it.test(message) }
    }

    companion object : ReplyTriggerParser<MessageChain> {

        override val uniqueId: String = "chain"

        override val arguments: Map<String, List<String>> = mapOf(
            "triggers" to listOf("触发器列表")
        )

        context(MessageEvent)
        override suspend fun parse(demand: Demand): ChainTrigger {
            TODO("Not yet implemented")
        }

        context(MessageEvent)
        override suspend fun createDefault(message: MessageChain, origin: MessageChain): ChainTrigger {
            return message
                .filterIsInstance<MessageContent>()
                .filter { it !is UnsupportedMessage }
                .map { Triggers.createDefault(it, origin) }
                .let(::ChainTrigger)
        }

    }

}
