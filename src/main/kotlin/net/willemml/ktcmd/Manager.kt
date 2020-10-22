package net.willemml.ktcmd

/**
 * A command manager for easily getting started
 */
open class CommandManager<T : Call>(
    /**
     * Prefix that should be checked for and removed when executing commands, if there is no prefix the command is still run whether or not [prefix] is empty.
     */
    val prefix: String = ""
) {
    private val commands = HashMap<String, Command<T>>()

    /**
     * Default help command to use
     */
    val helpCommand: Command<T> = Command("help", "Lists commands and gets the help/usage text for a command.", arrayListOf("h, ?")) {
        val commandName = getAnyArgument<String>("command")
        if (commandName != null) {
            val command = commands[commandName]
            if (command != null) {
                it.info(command.helpText())
            } else {
                it.error("No command with name $commandName.")
            }
        } else {
            it.info(listCommands())
        }
    }

    init {
        helpCommand.apply {
            string("command", false, "Command to get help/usage text of.", "c")
        }
        addCommand(helpCommand)
    }

    /**
     * Add a command for use by this manager
     */
    fun addCommand(command: Command<T>) {
        commands[command.name] = command
    }

    /**
     * Add an array of commands [commandsToAdd] to the manager
     */
    fun loadCommands(commandsToAdd: Array<Command<T>>) {
        for (command in commandsToAdd) {
            addCommand(command)
        }
    }

    /**
     * Gets a formatted list of commands that looks like:
     * Available Commands:
     *  - commandOne: Description of command one
     *  - commandTwo: Description of command two
     * @return formatted string of all the commands existing
     */
    open fun listCommands() =
        "Available Commands: ${commands.entries.joinToString(separator = "") { "\n - ${it.key}${if (it.value.description.isNotEmpty()) ": ${it.value.description}" else ""}" }}"

    /**
     * Runs a command based on [call], finds what command to execute by searching [commands] and then calls it, if the command fails it prints the error and then the help text of that command.
     */
    fun runCommand(call: T) {
        for (command in commands.values) {
            if (command.matches(call.callText) || command.matches(call.callText.removePrefix(prefix))) {
                try {
                    command.execute(call)
                } catch (e: RuntimeCommandSyntaxError) {
                    e.message?.let { call.error(it) }
                    call.info(command.helpText())
                }
                return
            }
        }
    }
}