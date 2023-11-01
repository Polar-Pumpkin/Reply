package me.parrot.mirai.listener

import kotlinx.coroutines.launch
import me.parrot.mirai.Reply
import me.parrot.mirai.data.History
import me.parrot.mirai.data.model.Link
import me.parrot.mirai.data.model.Response
import me.parrot.mirai.function.reply
import me.parrot.mirai.manager.Actions
import me.parrot.mirai.manager.Histories
import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.SimpleListenerHost
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.buildMessageChain
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.CoroutineContext

/**
 * Reply
 * me.parrot.mirai.listener.ReplyListener
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 16:26
 */
object ReplyListener : SimpleListenerHost() {

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        val cause = exception.rootCause
        cause.printStackTrace()

        val event = exception.event ?: return
        if (event is MessageEvent) {
            Reply.launch {
                event.reply {
                    +"执行操作时遇到错误:\n"
                    +"${cause::class.java.canonicalName}\n"
                    +"- - - - -\n"
                    +(cause.message ?: "null")
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    suspend fun MessageEvent.onMessage() {
        val scheduled = Actions[sender.id]
        if (scheduled != null) {
            return scheduled.run(this)
        }

        newSuspendedTransaction {
            val responses = Response.all()
                .filter { it.trigger.test(this@onMessage) }
                .associateBy { it.id.value }
                .toMutableMap()
                .takeIf { it.isNotEmpty() } ?: return@newSuspendedTransaction
            val links = Link.all()
                .filter { it.match(responses.keys) }
                .toList()
                .let(::CopyOnWriteArrayList)
            links.forEach {
                when (it.direction) {
                    Link.Direction.ONE_WAY -> {
                        if (it.active.id.value !in responses) {
                            responses -= it.passive.id.value
                            links -= it
                        }
                    }

                    Link.Direction.TWO_WAY -> {
                        val keys = setOf(it.active.id.value, it.passive.id.value)
                        if (!responses.keys.containsAll(keys)) {
                            responses -= keys
                            links -= it
                        }
                    }
                }
            }

            subject.sendMessage(buildMessageChain {
                val link = links.randomOrNull()
                if (link != null) {
                    link.active.content.append(this@onMessage)
                    link.passive.content.append(this@onMessage)
                    Histories[sender.id] = History(link)
                } else {
                    val response = responses.values.minBy { it.created }
                    response.content.append(this@onMessage)
                    Histories[sender.id] = History(response)
                }
            })
        }
    }

}