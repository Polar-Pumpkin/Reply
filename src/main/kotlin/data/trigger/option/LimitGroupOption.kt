package me.parrot.mirai.data.trigger.option

import me.parrot.mirai.Reply
import net.mamoe.mirai.console.permission.PermissionService

/**
 * Reply
 * me.parrot.mirai.data.trigger.option.LimitGroupOption
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 15:45
 */
data class LimitGroupOption(val groups: Set<Long>) : TriggerOption {

    companion object : TriggerOptionParser {

        override val uniqueId: String = "group"

        override val description: List<String> = listOf("有效群号, 以逗号分隔")

        val bypass by lazy {
            PermissionService.INSTANCE.register(
                Reply.permissionId("group-bypass"),
                "创建自动回复时无视群聊限制"
            )
        }

        override fun parse(content: String): LimitGroupOption {
            return LimitGroupOption(content.split(',').map(String::toLong).toSet())
        }

    }

}
