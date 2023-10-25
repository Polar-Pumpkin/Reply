package me.parrot

import me.parrot.command.CommandHandler
import me.parrot.data.context.ChainContext
import me.parrot.data.trigger.ReplyTrigger
import me.parrot.storage.ReplyContexts
import me.parrot.storage.ReplyDefines
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.MessageSource.Key.quote
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

    private val scheduled: MutableMap<Long, ReplyTrigger> = mutableMapOf()

    lateinit var db: Database
        private set

    override fun onEnable() {
        CommandManager.registerCommand(CommandHandler)
        val file = File(dataFolder, "data")
        // Database in file, needs full path or relative path starting with ./
        db = Database.connect("jdbc:h2:${file.absolutePath}", "org.h2.Driver")
        transaction(db) {
            SchemaUtils.create(ReplyContexts, ReplyDefines)
        }
        ReplyContexts.build()
        ReplyDefines.build()

        globalEventChannel().subscribeAlways<MessageEvent>(priority = EventPriority.MONITOR) listen@{
            val trigger = scheduled.remove(sender.id) ?: return@listen ReplyDefines.handle(it)
            val chain = ChainContext.of(message)
            if (chain.isNullOrEmpty()) {
                subject.sendMessage(message.quote() + "无法解析此内容, 本次编辑已取消")
                return@listen
            }
            val context = if (chain.size == 1) chain.first() else chain
            ReplyDefines.upload(trigger, context)
            subject.sendMessage(message.quote() + "已设置自动回复")
        }
    }

    fun schedule(userId: Long, trigger: ReplyTrigger) {
        scheduled[userId] = trigger
    }

    fun cancel(userId: Long): Boolean = scheduled.remove(userId) != null

}