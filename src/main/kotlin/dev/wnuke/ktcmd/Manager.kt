package dev.wnuke.ktcmd

class CommandManager<T : Call>(val prefix: String = "") {
    private val commands = HashMap<ArrayList<String>, Command<T>>()

    fun addCommand(command: Command<T>) {
        commands[command.aliases] = command
    }

    fun loadCommands(commandsToAdd: Array<Command<T>>) {
        for (command in commandsToAdd) {
            addCommand(command)
        }
    }

    fun listCommands() = "Available Commands: ${commands.entries.joinToString(separator = "") { "\n - ${it.key}${if (it.value.description.isNotEmpty()) ": ${it.value.description}" else ""}" }}"

    fun runCommand(commandString: String, call: T) {
        for (command in commands.values) {
            if (command.matches(commandString)) {
                try {
                    command.execute(call)
                } catch (e: RuntimeException) {
                    e.message?.let { call.error(it) }
                }
                return
            }
        }
    }
}