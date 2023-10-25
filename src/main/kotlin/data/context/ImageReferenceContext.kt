package me.parrot.data.context

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.parrot.storage.ReplyContexts
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.ImageType
import net.mamoe.mirai.message.data.MessageChain
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Reply
 * me.parrot.data.context.ImageReferenceContext
 * 从单项 ReplyContext 引用的用于复合的图像内容
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 14:24
 */
@Serializable
data class ImageReferenceContext(val index: Long) : ImageContext {

    constructor(entityId: EntityID<Long>) : this(entityId.value)

    @Transient
    private val delegate = ReplyContexts[EntityID(index, ReplyContexts)] as ImageLocalContext

    override val imageId: String
        get() = delegate.imageId
    override val imageType: ImageType
        get() = delegate.imageType
    override val size: Long
        get() = delegate.size
    override val width: Int
        get() = delegate.width
    override val height: Int
        get() = delegate.height
    override val isEmoji: Boolean
        get() = delegate.isEmoji
    override val md5: String
        get() = delegate.md5

    override suspend fun build(contact: Contact): MessageChain = delegate.build(contact)

    override fun match(context: ReplyContext): Boolean = delegate.match(context)

}
