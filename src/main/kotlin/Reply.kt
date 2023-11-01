package me.parrot.mirai

import me.parrot.mirai.command.ReplyCommand
import me.parrot.mirai.listener.ReplyListener
import me.parrot.mirai.manager.Caches
import me.parrot.mirai.storage.Binaries
import me.parrot.mirai.storage.Intervals
import me.parrot.mirai.storage.Links
import me.parrot.mirai.storage.Responses
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.registerTo
import net.mamoe.mirai.utils.info
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object Reply : KotlinPlugin(
    JvmPluginDescription(id = "me.parrot.mirai.reply", name = "Reply", version = "2.0.0") {
        author("legoshi")
        info("""Auto reply""")
    }
) {

    lateinit var db: Database
        private set

    override fun onEnable() {
        val file = File(dataFolder, "data")
        db = Database.connect("jdbc:h2:${file.absolutePath}", "org.h2.Driver")
        transaction(db) {
            SchemaUtils.create(Intervals, Binaries, Responses, Links)
        }
        logger.info { "已连接到数据库" }
        Caches.build()

        ReplyListener.registerTo(globalEventChannel())
        CommandManager.registerCommand(ReplyCommand)
    }

}