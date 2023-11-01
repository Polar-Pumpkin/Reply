package me.parrot.mirai.manager

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.parrot.mirai.Reply
import me.parrot.mirai.data.binary.BinaryMetadata
import me.parrot.mirai.data.content.Content
import me.parrot.mirai.data.model.BinaryResource
import me.parrot.mirai.data.model.Link
import me.parrot.mirai.data.model.Response
import me.parrot.mirai.data.trigger.ReplyTrigger
import me.parrot.mirai.function.reply
import me.parrot.mirai.storage.Responses
import net.mamoe.mirai.event.events.MessageEvent
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Reply
 * me.parrot.mirai.manager.Caches
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 14:14
 */
object Caches {

    private val binaries: MutableMap<Long, BinaryResource> = mutableMapOf()
    private val responses: MutableMap<Long, Response> = mutableMapOf()
    private val links: MutableList<Link> = mutableListOf()

    fun build() {
        binaries.clear()
        responses.clear()
        links.clear()
        transaction(Reply.db) {
            binaries += BinaryResource.all().associateBy { it.id.value }
            responses += Response.find { Responses.deleted.isNull() }.associateBy { it.id.value }
            links += Link.all()
        }
    }

    fun getBinaries(): Map<Long, BinaryResource> = binaries

    fun getBinary(binaryId: Long): BinaryResource =
        requireNotNull(binaries[binaryId]) { "Invalid binary resource: $binaryId" }

    fun newBinary(blob: ExposedBlob, meta: BinaryMetadata): BinaryResource {
        return transaction(Reply.db) {
            BinaryResource.new {
                value = blob
                metadata = meta
            }
        }.also { binaries[it.id.value] = it }
    }

    fun getResponses(): Map<Long, Response> = responses

    suspend fun excludeResponses(event: MessageEvent): Boolean {
        val responses = responses.values.filter { it.trigger.test(event) }
        if (responses.isNotEmpty()) {
            event.reply {
                +"此触发器已有 ${responses.size} 项自动回复:\n"
                responses.forEach {
                    +"#${it.id.value} ${it.trigger}"
                }
            }
            return false
        }
        return true
    }

    fun getResponseOrNull(responseId: Long): Response? = responses[responseId]

    fun newResponse(creatorId: Long, trigger: ReplyTrigger<*>, content: Content): Response {
        return transaction(Reply.db) {
            Response.new {
                this.trigger = trigger
                this.content = content
                this.creator = creatorId
            }
        }.also { responses[it.id.value] = it }
    }

    fun deleteResponse(response: Response) {
        responses -= response.id.value
        transaction(Reply.db) {
            response.deleted = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            response.flush()
        }
    }

    fun getLinks(): List<Link> = links

    fun collectLinks(responses: Set<Long>): List<Link> = links.filter { it.match(responses) }

    fun findLink(activeId: Long, passiveId: Long, direction: Link.Direction? = null): Link? {
        return links.firstOrNull {
            if (direction != null && it.direction != direction) {
                return@firstOrNull false
            }
            it.active.id.value == activeId && it.passive.id.value == passiveId
        }
    }

    fun newLink(active: Response, passive: Response, direction: Link.Direction): Link {
        return transaction(Reply.db) {
            Link.new {
                this.active = active
                this.passive = passive
                this.direction = direction
            }
        }.also { links += it }
    }

    fun deleteLink(link: Link) {
        links -= link
        transaction(Reply.db) {
            link.delete()
        }
    }

}