package dev.wnuke.ktcmd

open class Command<T : Call>(val name: String, val description: String = "", val aliases: List<String>) {
    private val arguments = HashMap<String, Argument<*>>()
    private val requiredArguments = HashSet<String>()
    private val parsedArguments = HashMap<String, Any>()

    init {
        aliases.plus(name)
    }

    fun string(name: String, required: Boolean = true, description: String = "", shortName: String = "", runs: (Call) -> Unit = {}): Command<T> {
        arguments[name] = StringArgument(name, description, runs, shortName)
        if (required) requiredArguments.add(name)
        return this
    }

    fun integer(name: String, required: Boolean = true, description: String = "", shortName: String = "", runs: (Call) -> Unit = {}): Command<T> {
        arguments[name] = IntArgument(name, description, runs, shortName)
        if (required) requiredArguments.add(name)
        return this
    }

    fun long(name: String, required: Boolean = true, description: String = "", shortName: String = "", runs: (Call) -> Unit = {}): Command<T> {
        arguments[name] = LongArgument(name, description, runs, shortName)
        if (required) requiredArguments.add(name)
        return this
    }

    fun float(name: String, required: Boolean = true, description: String = "", shortName: String = "", runs: (Call) -> Unit = {}): Command<T> {
        arguments[name] = FloatArgument(name, description, runs, shortName)
        if (required) requiredArguments.add(name)
        return this
    }

    fun double(name: String, required: Boolean = true, description: String = "", shortName: String = "", runs: (Call) -> Unit = {}): Command<T> {
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

    private fun getArgumentDescriptions(): Pair<HashMap<String, String>, HashMap<String, String>> {
        val required = HashMap<String, String>()
        val optional = HashMap<String, String>()
        for (arg in arguments) {
            if (requiredArguments.contains(arg.key)) required[arg.key] = arg.value.description
            else optional[arg.key] = arg.value.description
        }
        return Pair(required, optional)
    }

    fun helpText(): String {
        val args = getArgumentDescriptions()
        var help = "$name  $description"
        if (args.first.isNotEmpty()) help += "\n Required Arguments:"
        for (arg in args.first) {
            help += "\n  - ${arg.key}: ${arg.value}$"
        }
        if (args.second.isNotEmpty()) help += "\n Optional Arguments:"
        for ((name, description) in args.second) {
            help += "\n  - $name: $description$"
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
        val command = argumentString.split(Regex(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
        for (arg in command) {
            for ((name, argument) in arguments) {
                if (argument.matches(arg)) {
                    val parsed: Any? = if (argument.prefixOnly(arg)) {
                        if (command.last() != arg) {
                            parseArgument(command[command.indexOf(arg) + 1], argument)!!
                        } else {
                            if (requiredArguments.contains(name)) throw SyntaxError("$name requires a value") else null
                        }
                    } else parseArgument(arg, argument) ?: throw SyntaxError("$name is null")
                    if (parsed != null) parsedArguments[name] = parsed
                    break
                }
            }
        }
        onRun(call)
    }

    private fun <U, T : Argument<U>> parseArgument(string: String, arg: T): U {
        return arg.parse(string)
    }

    @Throws(SyntaxError::class)
    private inline fun <reified T> getArgument(string: String): T? {
        val argument = parsedArguments[string]
            ?: if (requiredArguments.contains(string)) throw RuntimeException("$string is a required argument for command $name and is missing") else return null
        if (argument is T) return argument
        else throw SyntaxError("argument $string is not the correct type")
    }

    open fun onRun(call: T) {}
}

open class Call(val callText: String) {
    fun respond(message: String) {
        println(message)
    }
}