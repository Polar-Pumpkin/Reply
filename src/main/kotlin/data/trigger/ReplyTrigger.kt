package me.parrot.data.trigger

import kotlinx.serialization.Serializable
import me.parrot.data.context.ImageContext
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageChain

/**
 * Reply
 * me.parrot.data.define.ReplyDefine
 * 自动回复触发器
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 09:33
 */
@Serializable
sealed interface ReplyTrigger {
    val text: String
    suspend fun test(message: MessageChain): Boolean
    suspend fun test(event: MessageEvent): Boolean = test(event.message)

    companion object {
        fun wrap(text: String): ReplyTrigger? {
            val args = text.split(':', limit = 2)
            return if (args.size == 1) {
                TextTrigger(args[0])
            } else {
                val prefix = args[0]
                val content = args[1]
                if (prefix.isEmpty()) {
                    TextTrigger(content)
                } else {
                    val char = prefix[0].lowercaseChar()
                    val chars = prefix.drop(1).toMutableList()
                    val ignoreSpace = chars.remove('~')
                    val ignoreCase = chars.remove('?')
                    when (char) {
                        'e' -> TextTrigger(content, TextTrigger.Mode.EQUAL, ignoreCase, ignoreSpace)
                        'c' -> TextTrigger(content, TextTrigger.Mode.CONTAIN, ignoreCase, ignoreSpace)
                        'p' -> TextTrigger(content, TextTrigger.Mode.PREFIX, ignoreCase, ignoreSpace)
                        's' -> TextTrigger(content, TextTrigger.Mode.SUFFIX, ignoreCase, ignoreSpace)
                        'r' -> RegexTrigger(content, ignoreCase, ignoreSpace)
                        else -> null
                    }
                }
            }
        }

        suspend fun wrap(image: Image): ReplyTrigger {
            return ImageTrigger(ImageContext.wrap(image))
        }
    }
}