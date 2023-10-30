package me.parrot.mirai

import me.parrot.mirai.storage.Binaries
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
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
            SchemaUtils.create(Binaries)
        }
        logger.info { "已连接到数据库" }
    }

}