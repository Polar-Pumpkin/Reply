package me.parrot.mirai.data.binary

import kotlinx.serialization.Serializable
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.ImageType
import java.util.*

/**
 * Reply
 * me.parrot.mirai.data.binary.ImageMetadata
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/27 14:27
 */
@Serializable
data class ImageMetadata(
    val imageId: String,
    val imageType: ImageType,
    val size: Long,
    val width: Int,
    val height: Int,
    val isEmoji: Boolean,
    val md5: String,
    val hash: String
) {
    companion object {
        fun of(image: Image, hash: String): ImageMetadata {
            return with(image) {
                ImageMetadata(
                    imageId, imageType,
                    size, width, height, isEmoji,
                    Base64.getEncoder().encodeToString(md5), hash
                )
            }
        }
    }
}
