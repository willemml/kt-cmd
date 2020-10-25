package net.willemml.ktcmd

import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap

open class Command<T : Call>(
    /**
     * Name of the command, is automatically appended to [aliases]
     */
    val name: String,
    val description: String = "",
    /**
     * Strings that can be used to call the command
     */
    val aliases: ArrayList<String> = ArrayList(),
    /**
     * If true only required arguments can be used and the must be given in order when the command is used
     * When true arguments do not require or look for their prefixes (--argname or -a) and instead parse each string in order
     */
    val parseUsingOrder: Boolean = false,
    /**
     * What to do when the command is run
     */
    val runs: Command<T>.(T) -> Unit
) {
    val arguments = LinkedHashMap<String, Triple<Argument<*, T>, Boolean, Any?>>()

    init {
        aliases.add(name)
    }

    /**
     * Adds this command to a command [manager]
     */
    fun addToManager(manager: CommandManager<T>) {
        manager.addCommand(this)
    }

    /**
     * Adds a String argument to the command with [name],
     * whether or not it is [required], a [description], a [shortName] to make the argument easier to use,
     * a [default] value and an execute block ([runs])
     * @throws IllegalArgumentException When there is an error in the code using this
     */
    @Throws(IllegalArgumentException::class)
    fun string(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        default: String = "",
        runs: T.(String) -> Unit = {}
    ): Command<T> {
        if (!required && parseUsingOrder) throw IllegalArgumentException("Argument $name of command ${this.name} cannot have an optional argument when parseUsingOrder is true.")
        val nameProcessed = name.trim().replace(' ', '_')
        if (arguments.containsKey(nameProcessed)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[nameProcessed] = Triple(
            StringArgument(nameProcessed, description, default, runs, shortName.trim().replace(' ', '_')),
            required,
            null
        )
        return this
    }

    /**
     * Adds a Boolean argument to the command with [name],
     * whether or not it is [required], a [description], a [shortName] to make the argument easier to use,
     * a [default] value and an execute block ([runs])
     * @throws IllegalArgumentException When there is an error in the code using this
     */
    @Throws(IllegalArgumentException::class)
    fun boolean(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        default: Boolean = false,
        runs: T.(Boolean) -> Unit = {}
    ): Command<T> {
        if (!required && parseUsingOrder) throw IllegalArgumentException("Argument $name of command ${this.name} cannot have an optional argument when parseUsingOrder is true.")
        val nameProcessed = name.trim().replace(' ', '_')
        if (arguments.containsKey(nameProcessed)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[nameProcessed] = Triple(
            BooleanArgument(nameProcessed, description, default, runs, shortName.trim().replace(' ', '_')),
            required,
            null
        )
        return this
    }

    /**
     * Adds an Integer argument to the command with [name],
     * whether or not it is [required], a [description], a [shortName] to make the argument easier to use,
     * a [default] value and an execute block ([runs])
     * @throws IllegalArgumentException When there is an error in the code using this
     */
    @Throws(IllegalArgumentException::class)
    fun integer(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        default: Int = -1,
        runs: T.(Int) -> Unit = {}
    ): Command<T> {
        if (!required && parseUsingOrder) throw IllegalArgumentException("Argument $name of command ${this.name} cannot have an optional argument when parseUsingOrder is true.")
        val nameProcessed = name.trim().replace(' ', '_')
        if (arguments.containsKey(nameProcessed)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[nameProcessed] = Triple(
            IntArgument(nameProcessed, description, default, runs, shortName.trim().replace(' ', '_')),
            required,
            null
        )
        return this
    }

    /**
     * Adds a Long argument to the command with [name],
     * whether or not it is [required], a [description], a [shortName] to make the argument easier to use,
     * a [default] value and an execute block ([runs])
     * @throws IllegalArgumentException When there is an error in the code using this
     */
    @Throws(IllegalArgumentException::class)
    fun long(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        default: Long = -1,
        runs: T.(Long) -> Unit = {}
    ): Command<T> {
        if (!required && parseUsingOrder) throw IllegalArgumentException("Argument $name of command ${this.name} cannot have an optional argument when parseUsingOrder is true.")
        val nameProcessed = name.trim().replace(' ', '_')
        if (arguments.containsKey(nameProcessed)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[nameProcessed] = Triple(
            LongArgument(nameProcessed, description, default, runs, shortName.trim().replace(' ', '_')),
            required,
            null
        )
        return this
    }

    /**
     * Adds a Float argument to the command with [name],
     * whether or not it is [required], a [description], a [shortName] to make the argument easier to use,
     * a [default] value and an execute block ([runs])
     * @throws IllegalArgumentException When there is an error in the code using this
     */
    @Throws(IllegalArgumentException::class)
    fun float(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        default: Float = -1.0F,
        runs: T.(Float) -> Unit = {}
    ): Command<T> {
        if (!required && parseUsingOrder) throw IllegalArgumentException("Argument $name of command ${this.name} cannot have an optional argument when parseUsingOrder is true.")
        val nameProcessed = name.trim().replace(' ', '_')
        if (arguments.containsKey(nameProcessed)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[nameProcessed] = Triple(
            FloatArgument(nameProcessed, description, default, runs, shortName.trim().replace(' ', '_')),
            required,
            null
        )
        return this
    }

    /**
     * Adds a Double argument to the command with [name],
     * whether or not it is [required], a [description], a [shortName] to make the argument easier to use,
     * a [default] value and an execute block ([runs])
     * @throws IllegalArgumentException When there is an error in the code using this
     */
    @Throws(IllegalArgumentException::class)
    fun double(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        default: Double = -1.0,
        runs: T.(Double) -> Unit = {}
    ): Command<T> {
        if (!required && parseUsingOrder) throw IllegalArgumentException("Argument $name of command ${this.name} cannot have an optional argument when parseUsingOrder is true.")
        val nameProcessed = name.trim().replace(' ', '_')
        if (arguments.containsKey(nameProcessed)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[nameProcessed] = Triple(
            DoubleArgument(nameProcessed, description, default, runs, shortName.trim().replace(' ', '_')),
            required,
            null
        )
        return this
    }

    /**
     * Check if [string] matches any of the commands aliases/names
     * @return Whether or not it matches
     */
    fun matches(string: String): Boolean {
        for (alias in aliases) {
            if (string.startsWith("$alias ") || string == alias) return true
        }
        return false
    }

    /**
     * Cached value of the commands help message for performance, can be cleared by setting to ""
     */
    var helpCache = ""

    /**
     * Gets the help text for this command, with a format of:
     * name: Description of command.
     *   Required Arguments:
     *     prefix [shortPrefix] (type): Description of required argument
     *   Optional Arguments:
     *     prefix [shortPrefix] (type): Description of optional argument
     *
     * or if [parseUsingOrder] is enabled:
     * name: Description of command.
     *   Arguments:
     *
     * @return The previous as a formatted String using newlines as line breaks
     */
    open fun helpText(): String {
        if (helpCache.isEmpty()) {
            val required = LinkedHashMap<String, String>()
            val optional = LinkedHashMap<String, String>()
            for (arg in arguments) {
                val argNameString =
                    if (parseUsingOrder) "- ${arg.key} (${arg.value.first.type.simpleName})"
                    else "--${arg.key} [${if (arg.value.first.shortName.isNotEmpty()) arg.value.first.shortPrefix else ""}] (${arg.value.first.type.simpleName})"
                if (arg.value.second) required[argNameString] = arg.value.first.description
                else optional[argNameString] = arg.value.first.description
            }
            helpCache = "$name: $description"
            if (required.isNotEmpty()) helpCache += if (parseUsingOrder) "\n  Arguments:" else "\n  Required Arguments:"
            for ((name, description) in required) {
                helpCache += "\n    $name: $description"
            }
            if (optional.isNotEmpty()) helpCache += "\n  Optional Arguments:"
            for ((name, description) in optional) {
                helpCache += "\n    $name: $description"
            }
            if (parseUsingOrder) {
                helpCache += "\n  Format: $name ${arguments.entries.joinToString("") { "<${it.key}> " }}"
            }
        }
        return helpCache
    }

    /**
     * Execute the command with [call], parses arguments, executes per argument execute blocks and executes the main execute block
     * @throws IllegalArgumentException When there is an issue with the code implementing this class
     * @throws RuntimeCommandSyntaxError When a supplied value for an argument is of the wrong type or when an argument is missing
     */
    @Throws(RuntimeCommandSyntaxError::class, IllegalArgumentException::class)
    fun execute(call: T) {
        var argumentString = ""
        for (alias in aliases) {
            if (matches(call.callText)) {
                argumentString = call.callText.removePrefix(alias).removePrefix(" ")
                break
            }
        }
        val command = Regex("(?<=\")[^\"]*(?=\")|[^\" ]+").findAll(argumentString).map { it.value }.filter { it.isNotEmpty() }.toMutableList()
        if (parseUsingOrder) {
            var i = 1
            for (arg in arguments) {
                if (command.size > i) {
                    val parsed: Any? = parseArgument(command[i], arg.value.first)
                    val argumentParsed = Triple(arg.value.first, arg.value.second, parsed)
                    arguments[arg.key] = argumentParsed
                    if (parsed != null) {
                        runArgument(call, argumentParsed)
                    }
                    i++
                } else throw RuntimeCommandSyntaxError("Argument ${arg.key} of ${this.name} is missing.")
            }
        } else {
            val argsDone = HashSet<String>()
            for (arg in command) {
                for ((name, argument) in arguments.filter { !argsDone.contains(it.key) }) {
                    arguments[name] = Triple(argument.first, argument.second, null)
                    if (argument.first.matches(arg)) {
                        val parsed: Any? =
                            if (argument.first.prefixOnly(arg)) if (arg != command.last()) parseArgument(
                                command.elementAt(
                                    command.indexOf(arg) + 1
                                ), argument.first
                            )!!
                            else null
                            else parseArgument(arg, argument.first)
                        if (argument.second && parsed == null) throw RuntimeCommandSyntaxError("Argument $name of ${this.name} requires a value.")
                        val argumentParsed = Triple(argument.first, argument.second, parsed)
                        arguments[name] = argumentParsed
                        if (parsed != null) {
                            runArgument(call, argumentParsed)
                        }
                        argsDone.add(name)
                        break
                    }
                }
            }
        }
        run(call)
    }

    /**
     * Runs the execute block of the command with [call] and the argument's meta in [argument]
     */
    open fun <S> runArgument(call: T, argument: Triple<Argument<*, T>, Boolean, S>) {
        if (argument.third != null) {
            if (argument.first.type == argument.third!!::class) {
                (argument.first as Argument<S, T>).runs.invoke(call, argument.third)
            }
        }
    }

    /**
     * Runs the execute block of the command with [call]
     * @throws IllegalArgumentException When there is an issue with the code implementing this class
     * @throws RuntimeCommandSyntaxError When a supplied value for an argument is of the wrong type or when an argument is missing
     */
    @Throws(IllegalArgumentException::class, RuntimeCommandSyntaxError::class)
    open fun run(call: T) {
        runs.invoke(this, call)
    }

    /**
     * Parses [string] to get the value of type [U] for the argument [arg]
     * @return The parsed value of [string] as [U]
     * @throws RuntimeCommandSyntaxError when the argument value given by the user is of the wrong type
     */
    @Throws(RuntimeCommandSyntaxError::class)
    fun <U, S : Argument<U, T>> parseArgument(string: String, arg: S): U {
        return arg.parse(string)
    }

    /**
     * Gets parsed argument with name [string] with type [T], returning null unless [useDefault] is true in which case it returns [string]'s default value
     * @return The parsed value of the argument, the default value or null depending on the arguments
     * @throws IllegalArgumentException when the wrong type is specified in the code or the argument was never created in the project code
     */
    @Throws(IllegalArgumentException::class)
    inline fun <reified T> getAnyArgument(string: String, useDefault: Boolean = false): T? {
        val argument = arguments[string] ?: throw IllegalArgumentException("$string is not an argument of $name.")
        val value = argument.third
            ?: return if (useDefault && argument.first.default is T) argument.first.default as T else null
        if (value is T) return value
        throw IllegalArgumentException("Argument $string of $name is not of type ${T::class.simpleName}.")
    }


    /**
     * Gets optional argument [string] of type [T], always returning default if otherwise null, shorthand for [getAnyArgument] with "useDefault" set to true
     * @return The parsed value of the argument or the default value
     */
    @Throws(IllegalArgumentException::class)
    inline fun <reified T> getOptionalArgument(string: String): T = getAnyArgument(string, true)!!

    /**
     * Gets required argument [string] of type [T] using [getAnyArgument]
     * @return The parsed value of the argument
     * @throws RuntimeCommandSyntaxError when the argument is missing
     */
    @Throws(IllegalArgumentException::class, RuntimeCommandSyntaxError::class)
    inline fun <reified T> getArgument(string: String): T {
        return getAnyArgument(string, false) ?: throw RuntimeCommandSyntaxError("Argument $string is missing.")
    }
}

abstract class Call(var callText: String) {
    abstract fun respond(message: String)
    open fun error(message: String) = respond(message)
    open fun success(message: String) = respond(message)
    open fun info(message: String) = respond(message)
}