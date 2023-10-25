package me.parrot.data.context

import kotlinx.serialization.Serializable
import me.parrot.storage.ReplyContexts
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.ImageType
import java.util.*

/**
 * Reply
 * me.parrot.data.context.ImageContext
 * 自定义图片内容
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/25 13:07
 */
@Serializable
sealed interface ImageContext : ReplyContext {
    val imageId: String
    val imageType: ImageType
    val size: Long
    val width: Int
    val height: Int
    val isEmoji: Boolean
    val md5: String

    val image: Image
        get() = Image.newBuilder(imageId).also {
            it.type = imageType
            it.size = size
            it.width = width
            it.height = height
            it.isEmoji = isEmoji
        }.build()

    companion object {
        suspend fun remote(image: Image): ImageRemoteContext {
            return ImageRemoteContext(
                image.imageId,
                image.imageType,
                image.size,
                image.width,
                image.height,
                image.isEmoji,
                Base64.getEncoder().encodeToString(image.md5),
                image.queryUrl()
            )
        }

        suspend fun wrap(image: Image): ImageReferenceContext {
            val remote = remote(image)
            val exist = ReplyContexts.matchIsInstance<ImageLocalContext>(remote)
            if (exist != null) {
                return ImageReferenceContext(exist)
            }

            val local = remote.download()
            val similar = ReplyContexts.matchIsInstance<ImageLocalContext>(local)
            if (similar != null) {
                return ImageReferenceContext(similar)
            }
            return ImageReferenceContext(ReplyContexts.upload(local))
        }
    }
}
