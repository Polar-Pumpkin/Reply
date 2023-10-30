package me.parrot.mirai.data.trigger.option

import me.parrot.mirai.data.trigger.ReplyTrigger
import me.parrot.mirai.internal.flag.Unique

/**
 * Reply
 * me.parrot.mirai.data.trigger.option.TriggerOptionParser
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 09:37
 */
interface TriggerOptionParser : Unique<String> {

    val description: List<String>

    val targets: Set<Class<out ReplyTrigger<*>>>
        get() = emptySet()

    fun parse(content: String): TriggerOption?

}