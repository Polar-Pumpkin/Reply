package me.parrot.mirai

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object Reply : KotlinPlugin(
    JvmPluginDescription(
        id = "me.parrot.mirai.reply",
        name = "Reply",
        version = "2.0.0",
    ) {

        author("legoshi")
        info("""Auto reply""")
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
    }
}