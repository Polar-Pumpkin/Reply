package me.parrot.data.trigger

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content

/**
 * Reply
 * me.parrot.data.trigger.RegexTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 14:56
 */
@Serializable
data class RegexTrigger(
    val pattern: String,
    val ignoreCase: Boolean = false,
    val ignoreSpace: Boolean = false
) : ReplyTrigger {

    @Transient
    private val regex = Regex(pattern, buildSet {
        if (ignoreCase) {
            add(RegexOption.IGNORE_CASE)
        }
    })

    override suspend fun test(message: MessageChain): Boolean {
        val line = message.content
        return regex.containsMatchIn(if (ignoreSpace) line.trim() else line)
    }

}
