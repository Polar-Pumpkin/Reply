package me.parrot.mirai.registry

import me.parrot.mirai.data.trigger.*
import me.parrot.mirai.internal.container.Registry

/**
 * Reply
 * me.parrot.mirai.registry.Triggers
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 11:28
 */
object Triggers : Registry<String, ReplyTriggerParser<*>>() {

    init {
        register(AtTrigger)
        register(TextTrigger)
        register(ImageTrigger)
        register(IntervalTrigger)
    }

}