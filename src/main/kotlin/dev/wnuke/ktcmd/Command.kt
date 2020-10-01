package dev.wnuke.ktcmd

open class Command<T : Call>(
    val name: String,
    val description: String = "",
    val aliases: ArrayList<String> = ArrayList(),
    val runs: Command<T>.(T) -> Unit
) {
    var arguments = HashMap<String, Triple<Argument<*, T>, Boolean, Any?>>()

    init {
        aliases.add(name)
    }

    fun addToManager(manager: CommandManager<T>) {
        manager.addCommand(this)
    }

    @Throws(IllegalArgumentException::class)
    fun string(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        runs: T.(String) -> Unit = {}
    ): Command<T> {
        if (arguments.containsKey(name)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[name] = Triple(StringArgument(name, description, runs, shortName), required, null)
        return this
    }

    @Throws(IllegalArgumentException::class)
    fun integer(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        runs: T.(Int) -> Unit = {}
    ): Command<T> {
        if (arguments.containsKey(name)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[name] = Triple(IntArgument(name, description, runs, shortName), required, null)
        return this
    }

    @Throws(IllegalArgumentException::class)
    fun long(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        runs: T.(Long) -> Unit = {}
    ): Command<T> {
        if (arguments.containsKey(name)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[name] = Triple(LongArgument(name, description, runs, shortName), required, null)
        return this
    }

    @Throws(IllegalArgumentException::class)
    fun float(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        runs: T.(Float) -> Unit = {}
    ): Command<T> {
        if (arguments.containsKey(name)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[name] = Triple(FloatArgument(name, description, runs, shortName), required, null)
        return this
    }

    @Throws(IllegalArgumentException::class)
    fun double(
        name: String,
        required: Boolean = true,
        description: String = "",
        shortName: String = "",
        runs: T.(Double) -> Unit = {}
    ): Command<T> {
        if (arguments.containsKey(name)) throw IllegalArgumentException("There is already an argument called $name.")
        arguments[name] = Triple(DoubleArgument(name, description, runs, shortName), required, null)
        return this
    }

    fun matches(string: String): Boolean {
        for (alias in aliases) {
            if (string.startsWith("$alias ")) return true
        }
        return false
    }

    open fun helpText(): String {
        val required = HashMap<String, String>()
        val optional = HashMap<String, String>()
        for (arg in arguments) {
            if (arg.value.second) required[arg.key] = arg.value.first.description
            else optional["${arg.key} (${arg.value.first.type.simpleName})"] = arg.value.first.description
        }
        var help = "$name: $description"
        if (required.isNotEmpty()) help += "\n Required Arguments:"
        for ((name, description) in required) {
            help += "\n  - $name: $description"
        }
        if (optional.isNotEmpty()) help += "\n Optional Arguments:"
        for ((name, description) in optional) {
            help += "\n  - $name: $description"
        }
        return help
    }

    @Throws(RuntimeCommandSyntaxError::class, IllegalArgumentException::class)
    fun execute(call: T) {
        var argumentString = ""
        for (alias in aliases) {
            if (matches(call.callText)) {
                argumentString = call.callText.removePrefix("$alias ")
                break
            }
        }
        val command = Regex("(?<=\")[^\"]*(?=\")|[^\" ]+").findAll(argumentString).toList()
        for (argMatch in command) {
            val arg = argMatch.value
            for ((name, argument) in arguments) {
                arguments[name] = Triple(argument.first, argument.second, null)
                if (argument.first.matches(arg)) {
                    val parsed: Any? =
                        if (argument.first.prefixOnly(arg)) if (command.last() != argMatch) parseArgument(
                            command[command.indexOf(argMatch) + 1].value, argument.first
                        )!! else null
                        else parseArgument(arg, argument.first)
                    if (argument.second && parsed == null) throw RuntimeCommandSyntaxError("Argument $name of ${this.name} requires a value.")
                    val argumentParsed = Triple(argument.first, argument.second, parsed)
                    arguments[name] = argumentParsed
                    if (parsed != null) {
                        runArgument(call, argumentParsed)
                    }
                }
            }
        }
        run(call)
    }

    open fun <S> runArgument(call: T, argument: Triple<Argument<*, T>, Boolean, S>) {
        if (argument.third != null) {
            if (argument.first.type == argument.third!!::class) {
                (argument.first as Argument<S, T>).runs.invoke(call, argument.third)
            }
        }
    }

    open fun run(call: T) {
        runs.invoke(this, call)
    }

    @Throws(RuntimeCommandSyntaxError::class)
    fun <U, S : Argument<U, T>> parseArgument(string: String, arg: S): U {
        return arg.parse(string)
    }

    @Throws(IllegalArgumentException::class)
    inline fun <reified T> getOptionalArgument(string: String): T? {
        if (arguments.containsKey(string)) {
            return if (arguments[string]?.third != null) getArgument<T>(string)
            else null
        }
        throw IllegalArgumentException("$string is not an argument of $name.")
    }

    @Throws(IllegalArgumentException::class)
    inline fun <reified T> getArgument(string: String): T {
        val argument = arguments[string] ?: throw IllegalArgumentException("$string is not an argument of $name.")
        val value =
            argument.third ?: throw IllegalArgumentException("Attempting to get an optional argument that is null.")
        if (value is T) return value
        throw IllegalArgumentException("Argument $string of $name is not of type ${T::class.simpleName}.")
    }
}

abstract class Call(val callText: String) {
    abstract fun respond(message: String)
    open fun error(message: String) = respond(message)
    open fun success(message: String) = respond(message)
    open fun info(message: String) = respond(message)
}