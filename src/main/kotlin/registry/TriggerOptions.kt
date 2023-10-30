package me.parrot.mirai.registry

import me.parrot.mirai.data.Demand
import me.parrot.mirai.data.trigger.ReplyTrigger
import me.parrot.mirai.data.trigger.option.InstanceExclusiveOption
import me.parrot.mirai.data.trigger.option.InstanceSingletonOption
import me.parrot.mirai.data.trigger.option.TriggerOption
import me.parrot.mirai.data.trigger.option.TriggerOptionParser
import me.parrot.mirai.internal.container.Registry

/**
 * Reply
 * me.parrot.mirai.registry.TriggerOptions
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 09:44
 */
object TriggerOptions : Registry<String, TriggerOptionParser>() {

    init {
        register(InstanceSingletonOption)
        register(InstanceExclusiveOption)
    }

    fun target(clazz: Class<out ReplyTrigger<*>>): List<TriggerOptionParser> =
        values.filter { it.targets.isEmpty() || clazz in it.targets }

    fun parse(clazz: Class<out ReplyTrigger<*>>, demand: Demand): List<TriggerOption> {
        val values = mutableMapOf<String, String>()
        values += demand.arguments
        values += demand.flags.associateWith { "true" }
        return target(clazz).mapNotNull { it.parse(values[it.uniqueId] ?: return@mapNotNull null) }
    }

}