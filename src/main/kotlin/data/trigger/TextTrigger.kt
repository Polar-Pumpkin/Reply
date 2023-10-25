package me.parrot.data.trigger

import kotlinx.serialization.Serializable
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content

/**
 * Reply
 * me.parrot.data.trigger.TextTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 14:50
 */
@Serializable
data class TextTrigger(
    val text: String,
    val mode: Mode = Mode.EQUAL,
    val ignoreCase: Boolean = false,
    val ignoreSpace: Boolean = false
) : ReplyTrigger {

    override suspend fun test(message: MessageChain): Boolean {
        var line = message.content
        if (ignoreSpace) {
            line = line.trim()
        }
        return when (mode) {
            Mode.EQUAL -> line.equals(text, ignoreCase)
            Mode.CONTAIN -> line.contains(text, ignoreCase)
            Mode.PREFIX -> line.startsWith(text, ignoreCase)
            Mode.SUFFIX -> line.endsWith(text, ignoreCase)
        }
    }

    enum class Mode {
        EQUAL,
        CONTAIN,
        PREFIX,
        SUFFIX;
    }

}
