package me.parrot

import me.parrot.command.ReplyCommand
import me.parrot.data.action.ScheduledAction
import me.parrot.function.responsive
import me.parrot.storage.ReplyContexts
import me.parrot.storage.ReplyDefines
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.globalEventChannel
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object Reply : KotlinPlugin(
    JvmPluginDescription(id = "me.parrot.reply", name = "Reply", version = "1.0.0") {
        author("EntityParrot_")
        info("""Auto reply""")
    }
) {

    private val scheduled: MutableMap<Long, ScheduledAction> = mutableMapOf()

    lateinit var db: Database
        private set

    override fun onEnable() {
        CommandManager.registerCommand(ReplyCommand)

        val file = File(dataFolder, "data")
        db = Database.connect("jdbc:h2:${file.absolutePath}", "org.h2.Driver")
        transaction(db) {
            SchemaUtils.create(ReplyContexts, ReplyDefines)
        }
        ReplyContexts.build()
        ReplyDefines.build()

        globalEventChannel().subscribeAlways<MessageEvent>(priority = EventPriority.MONITOR) listen@{
            val action = scheduled.remove(sender.id) ?: return@listen ReplyDefines.handle(it)
            responsive {
                action.execute(it)
            }
        }
    }

    fun schedule(userId: Long, action: ScheduledAction) {
        scheduled[userId] = action
    }

    fun cancel(userId: Long): Boolean = scheduled.remove(userId) != null

}