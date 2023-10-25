package me.parrot.data.context

import kotlinx.serialization.Serializable
import me.parrot.algorithm.dhash.DHash
import me.parrot.function.unzip
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Image.Key.isUploaded
import net.mamoe.mirai.message.data.ImageType
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.toMessageChain
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.util.*

@Serializable
data class ImageLocalContext(
    override val imageId: String,
    override val imageType: ImageType,
    override val size: Long,
    override val width: Int,
    override val height: Int,
    override val isEmoji: Boolean,
    override val md5: String,
    val data: String,
    val hash: String
) : ImageContext {

    constructor(remote: ImageRemoteContext, data: String, hash: String) : this(
        remote.imageId,
        remote.imageType,
        remote.size,
        remote.width,
        remote.height,
        remote.isEmoji,
        remote.md5,
        data,
        hash
    )

    private val resource: ExternalResource
        get() = Base64.getDecoder().decode(data).unzip().toExternalResource(imageId)

    override suspend fun build(contact: Contact): MessageChain {
        return (image.takeIf { it.isUploaded(contact.bot) } ?: contact.uploadImage(resource)).toMessageChain()
    }

    override fun match(context: ReplyContext): Boolean {
        if (context !is ImageContext) return false
        if (context.imageId == imageId || context.md5 == md5) return true
        if (context !is ImageLocalContext) return false
        return DHash.diff(hash, context.hash) <= 25
    }

}