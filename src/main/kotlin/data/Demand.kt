package me.parrot.mirai.data

/**
 * Reply
 * me.parrot.mirai.data.Demand
 *
 * @author legoshi
 * @version 1
 * @since 2023/10/30 10:26
 */
data class Demand(
    val namespace: String,
    val positions: List<String> = emptyList(),
    val arguments: Map<String, String> = emptyMap(),
    val flags: Set<String> = emptySet()
) {

    fun argumentOrNull(name: String, index: Int = -1, defaultValue: String? = null): String? {
        arguments[name]?.let {
            return it
        }
        if (name in flags) {
            return "true"
        }
        if (index >= 0) {
            positions.elementAtOrNull(index)?.let {
                return it
            }
        }
        return defaultValue
    }

    fun argument(name: String, index: Int = -1, defaultValue: String? = null): String {
        return checkNotNull(argumentOrNull(name, index, defaultValue)) { "'$name'($index) is required" }
    }

    fun flag(name: String, index: Int = -1, defaultValue: Boolean = false): Boolean {
        return argumentOrNull(name, index, "false")?.toBooleanStrict() ?: return defaultValue
    }

    companion object {
        fun read(define: String): Demand {
            val namespace = StringBuilder()
            val positions = mutableListOf<String>()
            val arguments = mutableMapOf<String, String>()
            val flags = mutableSetOf<String>()

            val builder = StringBuilder()
            val argument = StringBuilder()

            fun stage() {
                val value = builder.toString()
                builder.clear()
                when {
                    namespace.isEmpty() -> namespace.append(value)

                    value.startsWith('-') -> {
                        val arg = value.substring(1)
                        if (argument.isNotEmpty()) {
                            flags += argument.toString()
                            argument.clear()
                        }
                        argument.append(arg)
                    }

                    argument.isNotEmpty() -> {
                        val arg = argument.toString()
                        argument.clear()
                        arguments[arg] = value
                    }

                    else -> positions += value
                }
            }

            var isEscaped = false
            var isQuoted = false
            define.forEach { char ->
                when {
                    isEscaped -> {
                        isEscaped = false
                        builder.append(char)
                    }

                    char == '\\' -> isEscaped = true
                    char == '"' -> isQuoted = !isQuoted
                    isQuoted -> builder.append(char)
                    char == ' ' -> stage()
                    else -> builder.append(char)
                }
            }
            stage()
            return Demand(namespace.toString(), positions, arguments, flags)
        }

    }

}
