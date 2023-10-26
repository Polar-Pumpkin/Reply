package me.parrot.data.trigger

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.parrot.data.context.ImageContext
import me.parrot.data.context.ImageLocalContext
import me.parrot.data.context.ImageReferenceContext
import me.parrot.storage.ReplyContexts
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.MessageChain
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Reply
 * me.parrot.data.trigger.ImageTrigger
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 14:59
 */
@Serializable
data class ImageTrigger(val index: Long) : ReplyTrigger {

    constructor(reference: ImageReferenceContext) : this(reference.index)

    @Transient
    override val text: String = "(图片)"

    @Transient
    private val ref = ReplyContexts[EntityID(index, ReplyContexts)] as ImageLocalContext

    override suspend fun test(message: MessageChain): Boolean {
        val image = message.filterIsInstance<Image>().takeIf { it.size == 1 }?.first() ?: return false
        val remote = ImageContext.remote(image)
        return ref.match(remote) || ref.match(remote.download())
    }

}
