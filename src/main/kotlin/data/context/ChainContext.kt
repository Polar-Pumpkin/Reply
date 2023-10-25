package me.parrot.data.context

import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Reply
 * me.parrot.data.context.ChainContext
 * 一组不同类型的内容
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 13:59
 */
@Serializable
data class ChainContext(val chain: List<ReplyContext>) : ReplyContext, List<ReplyContext> by chain {

    override suspend fun build(contact: Contact): MessageChain {
        return chain.map { it.build(contact) }.toMessageChain()
    }

    override fun match(context: ReplyContext): Boolean {
        if (context !is ChainContext) return false
        if (context.chain.size != chain.size) return false
        val other = CopyOnWriteArrayList(context.chain)
        chain.forEach { ref ->
            other.remove(other.find { it.match(ref) } ?: return false)
        }
        return true
    }

    companion object {
        suspend fun of(chain: MessageChain): ChainContext? {
            return ChainContext(buildList {
                chain.filterIsInstance<MessageContent>().forEach { message ->
                    this += when (message) {
                        is UnsupportedMessage -> return@forEach
                        is PlainText -> TextContext(message.content)
                        is Image -> ImageContext.wrap(message)
                        else -> return null
                    }
                }
            })
        }
    }

}