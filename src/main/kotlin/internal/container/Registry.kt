package me.parrot.mirai.internal.container

import me.parrot.mirai.internal.flag.Unique

/**
 * Reply
 * me.parrot.mirai.internal.container.Registry
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 10:15
 */
open class Registry<K, V : Unique<K>>(private val internal: MutableMap<K, V> = mutableMapOf()) : Map<K, V> by internal {

    fun register(value: V, force: Boolean = false) {
        check(force || value.uniqueId !in internal) { "Duplicate: ${value.uniqueId}" }
        internal[value.uniqueId] = value
    }

    fun unregister(key: K): V? = internal.remove(key)

}