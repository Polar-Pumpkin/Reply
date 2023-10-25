package me.parrot.data.context

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import me.parrot.algorithm.dhash.DHash
import me.parrot.function.zip
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.Image.Key.isUploaded
import net.mamoe.mirai.message.data.ImageType
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.toMessageChain
import java.io.ByteArrayInputStream
import java.net.URL
import java.util.*
import java.util.zip.GZIPInputStream
import javax.imageio.ImageIO

@Serializable
data class ImageRemoteContext(
    override val imageId: String,
    override val imageType: ImageType,
    override val size: Long,
    override val width: Int,
    override val height: Int,
    override val isEmoji: Boolean,
    override val md5: String,
    val url: String
) : ImageContext {

    suspend fun download(): ImageLocalContext {
        val bytes = withContext(Dispatchers.IO) { URL(url).openConnection() }.run {
            addRequestProperty(
                "user-agent",
                "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36"
            )
            withContext(Dispatchers.IO) { getInputStream() }.run {
                if (contentEncoding == "gzip") GZIPInputStream(this) else this
            }.readBytes()
        }
        val data = Base64.getEncoder().encodeToString(bytes.zip())
        val image = withContext(Dispatchers.IO) {
            ImageIO.read(ByteArrayInputStream(bytes))
        }
        val hash = DHash.compute(image).toString()
        return ImageLocalContext(this, data, hash)
    }

    override suspend fun build(contact: Contact): MessageChain {
        return image.takeIf { it.isUploaded(contact.bot) }?.toMessageChain() ?: download().build(contact)
    }

    override fun match(context: ReplyContext): Boolean {
        return context is ImageContext && (context.imageId == imageId || context.md5 == md5)
    }

}