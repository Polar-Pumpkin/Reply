package me.parrot.mirai.function

import me.parrot.mirai.data.trigger.option.TriggerOption
import java.util.*

/**
 * Reply
 * me.parrot.mirai.function.Value
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 09:41
 */
fun <T : TriggerOption> optionId(clazz: Class<T>): String = clazz.simpleName.substringBefore("Option")

inline fun <reified T : TriggerOption> optionId(): String = optionId(T::class.java)

fun base64(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)