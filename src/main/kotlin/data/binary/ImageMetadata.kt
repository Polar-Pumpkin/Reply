package me.parrot.mirai.data.binary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import me.parrot.mirai.Reply.logger
import me.parrot.mirai.algorithm.DHash
import me.parrot.mirai.data.model.BinaryResource
import me.parrot.mirai.function.base64
import me.parrot.mirai.function.compress
import me.parrot.mirai.function.decompress
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.isUploaded
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.ImageType
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.function.Predicate
import java.util.zip.GZIPInputStream
import javax.imageio.ImageIO

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
) : BinaryMetadata {

    fun image(): Image {
        return Image.newBuilder(imageId).also {
            it.type = imageType
            it.size = size
            it.width = width
            it.height = height
            it.isEmoji = isEmoji
        }.build()
    }

    fun isSameImage(image: Image): Boolean {
        return image.imageId == imageId || base64(image.md5) == md5
    }

    fun isSimilar(meta: ImageMetadata): Boolean {
        return meta.imageId == imageId || meta.md5 == md5 || DHash.diff(hash, meta.hash) <= 10
    }

    suspend fun isSimilar(image: Image): Boolean {
        return isSameImage(image) || isSimilar(download(image).second)
    }

    suspend fun build(blob: ExposedBlob, contact: Contact): Image {
        val resource = blob.bytes.decompress().toExternalResource(imageId)
        return image().takeIf { it.isUploaded(contact.bot) } ?: resource.uploadAsImage(contact)
    }

    companion object {

        private val cached: MutableMap<String, Pair<ByteArray, ImageMetadata>> = mutableMapOf()

        private val userAgent = listOf(
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)",
            "AppleWebKit/537.36 (KHTML, like Gecko)",
            "Chrome/118.0.0.0",
            "Safari/537.36"
        ).joinToString(" ")

        suspend fun find(image: Image): BinaryResource? {
            return newSuspendedTransaction {
                val cache = cached[image.imageId]
                    ?.let { (_, meta) -> Predicate<ImageMetadata> { it.isSimilar(meta) } }
                    ?: Predicate<ImageMetadata> { it.isSameImage(image) }
                BinaryResource.all()
                    .asSequence()
                    .filter { it.metadata is ImageMetadata }
                    .find { cache.test(it.metadata as ImageMetadata) }
                    ?.let { return@newSuspendedTransaction it }
                val (_, meta) = download(image)
                BinaryResource.all()
                    .asSequence()
                    .filter { it.metadata is ImageMetadata }
                    .find { (it.metadata as ImageMetadata).isSimilar(meta) }
            }
        }

        suspend fun download(image: Image, cache: Boolean = true): Pair<ByteArray, ImageMetadata> {
            if (cache) {
                cached[image.imageId]?.let { return it }
            }
            val url = image.queryUrl()
            logger.info("准备下载图片:")
            logger.info(url)
            val connection = withContext(Dispatchers.IO) { URL(url).openConnection() }
            connection.addRequestProperty("user-agent", userAgent)

            val isZipped = connection.contentEncoding == "gzip"
            val raw = withContext(Dispatchers.IO) {
                connection.getInputStream().let { if (isZipped) GZIPInputStream(it) else it }.readBytes()
            }
            val img = withContext(Dispatchers.IO) { ImageIO.read(ByteArrayInputStream(raw)) }
            val hash = String(DHash.compute(img))

            val meta = of(image, hash)
            val bytes = raw.compress()
            val packet = bytes to meta
            cached[image.imageId] = packet
            return packet
        }

        suspend fun upload(image: Image): BinaryResource {
            val (bytes, meta) = download(image)
            return transaction {
                BinaryResource.new {
                    value = ExposedBlob(bytes)
                    metadata = meta
                }
            }
        }

        private fun of(image: Image, hash: String): ImageMetadata {
            return with(image) {
                ImageMetadata(
                    imageId, imageType,
                    size, width, height, isEmoji,
                    base64(md5), hash
                )
            }
        }

    }

}
