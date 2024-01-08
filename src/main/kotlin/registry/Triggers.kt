package me.parrot.mirai.registry

import me.parrot.mirai.data.Demand
import me.parrot.mirai.data.trigger.*
import me.parrot.mirai.internal.container.Registry
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*

/**
 * Reply
 * me.parrot.mirai.registry.Triggers
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 11:28
 */
object Triggers : Registry<String, ReplyTriggerParser<*, *>>() {

    init {
        register(AtTrigger)
        register(FaceTrigger)
        register(TextTrigger)
        register(ImageTrigger)
        register(IntervalTrigger)
        register(SenderTrigger)
        register(RandomTrigger)
    }

    context(MessageEvent)
    suspend fun parse(content: String): ReplyTrigger<*> {
        val triggers = content.split('\n')
            .map(Demand.Companion::read)
            .map {
                requireNotNull(Triggers[it.namespace]) {
                    "Unknown trigger: ${it.namespace}"
                }.parse(it)
            }
        require(triggers.isNotEmpty()) { "Unable to parse any triggers" }
        return if (triggers.size == 1) triggers.first() else ChainTrigger(triggers)
    }

    context(MessageEvent)
    suspend fun createDefault(message: Message, origin: MessageChain? = null): ReplyTrigger<out Message> {
        val trigger = when (message) {
            is At -> AtTrigger.createDefault(message, origin)
            is Face -> FaceTrigger.createDefault(message, origin)
            is Image -> ImageTrigger.createDefault(message, origin)
            is PlainText -> TextTrigger.createDefault(message, origin)
            is MessageChain -> ChainTrigger.createDefault(message, origin)
            else -> throw IllegalArgumentException("Unsupported message type: ${message::class.java.canonicalName}")
        }

        return if (trigger is ChainTrigger) {
            if (trigger.triggers.size == 1) {
                trigger.triggers.first()
            } else {
                trigger
            }
        } else {
            trigger
        }
    }

}