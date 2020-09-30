package dev.wnuke.ktcmd

open class CommandManager<T : Call>(val prefix: String = "") {
    private val commands = HashMap<String, Command<T>>()

    open val helpCommand: Command<T> = Command("help", "Lists commands and gets the help/usage text for a command.", arrayListOf("h, ?")) {
        val commandName = getArgument<String>("command")
        if (commandName != null) {
            val command = commands[name]
            if (command != null) {
                it.info(command.helpText())
            }
        } else {
            it.info(listCommands())
        }
    }

    init {
        addCommand(helpCommand)
    }

    fun addCommand(command: Command<T>) {
        commands[command.name] = command
    }

    fun loadCommands(commandsToAdd: Array<Command<T>>) {
        for (command in commandsToAdd) {
            addCommand(command)
        }
    }

    open fun listCommands() =
        "Available Commands: ${commands.entries.joinToString(separator = "") { "\n - ${it.key}${if (it.value.description.isNotEmpty()) ": ${it.value.description}" else ""}" }}"

    fun runCommand(call: T) {
        for (command in commands.values) {
            if (command.matches(call.callText)) {
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