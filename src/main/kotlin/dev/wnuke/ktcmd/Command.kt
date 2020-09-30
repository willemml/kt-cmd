package dev.wnuke.ktcmd

open class Command<T : Call>(val name: String, val description: String = "", val aliases: ArrayList<String> = ArrayList(), val runs: (Call) -> Unit) {
    val arguments = HashMap<String, Argument<*>>()
    val requiredArguments = HashSet<String>()
    val parsedArguments = HashMap<String, Any?>()

    init {
        aliases.add(name)
    }

    fun string(name: String, required: Boolean = true, description: String = "", shortName: String = "", runs: (String) -> Unit = {}): Command<T> {
        arguments[name] = StringArgument(name, description, runs, shortName)
        if (required) requiredArguments.add(name)
        return this
    }

    fun integer(name: String, required: Boolean = true, description: String = "", shortName: String = "", runs: (Int) -> Unit = {}): Command<T> {
        arguments[name] = IntArgument(name, description, runs, shortName)
        if (required) requiredArguments.add(name)
        return this
    }

    fun long(name: String, required: Boolean = true, description: String = "", shortName: String = "", runs: (Long) -> Unit = {}): Command<T> {
        arguments[name] = LongArgument(name, description, runs, shortName)
        if (required) requiredArguments.add(name)
        return this
    }

    fun float(name: String, required: Boolean = true, description: String = "", shortName: String = "", runs: (Float) -> Unit = {}): Command<T> {
        arguments[name] = FloatArgument(name, description, runs, shortName)
        if (required) requiredArguments.add(name)
        return this
    }

    fun double(name: String, required: Boolean = true, description: String = "", shortName: String = "", runs: (Double) -> Unit = {}): Command<T> {
        arguments[name] = DoubleArgument(name, description, runs, shortName)
        if (required) requiredArguments.add(name)
        return this
    }

    fun matches(string: String): Boolean {
        for (alias in aliases) {
            if (string.startsWith("$alias ")) return true
        }
        return false
    }

    fun helpText(): String {
        val required = HashMap<String, String>()
        val optional = HashMap<String, String>()
        for (arg in arguments) {
            if (requiredArguments.contains(arg.key)) required[arg.key] = arg.value.description
            else optional["${arg.key} (${arg.value.type.simpleName})"] = arg.value.description
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

    @Throws(SyntaxError::class)
    fun execute(call: T) {
        parsedArguments.clear()
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
                if (argument.matches(arg)) {
                    val parsed: Any? = if (argument.prefixOnly(arg)) {
                        if (command.last() != argMatch) {
                            parseArgument(command[command.indexOf(argMatch) + 1].value, argument)!!
                        } else {
                            if (requiredArguments.contains(name)) throw SyntaxError("$name requires a value") else null
                        }
                    } else parseArgument(arg, argument) ?: throw SyntaxError("$name is null")
                    parsedArguments[name] = parsed
                    if (parsed != null) {
                        runArgument(parsed, argument)
                    }
                }
            }
        }
        run(call)
    }

    fun <T> runArgument(value: T, argument: Argument<*>) {
        if (argument.type == value!!::class) (argument as Argument<T>).runs.invoke(value)
    }

    fun run(call: T) {
        runs.invoke(call)
    }

    fun <U, T : Argument<U>> parseArgument(string: String, arg: T): U {
        return arg.parse(string)
    }

    @Throws(SyntaxError::class)
    inline fun <reified T> getArgument(string: String): T? {
        val argument = parsedArguments[string]
            ?: if (requiredArguments.contains(string)) throw RuntimeException("$string is a required argument for command $name and is missing") else return null
        if (argument is T) return argument
        else throw SyntaxError("argument $string is not the correct type")
    }
}

open class Call(val callText: String) {
    fun respond(message: String) {
        println(message)
    }
}