package dev.wnuke.ktcmd

class SyntaxError(problem: String) : RuntimeException(problem)

abstract class Argument<T>(name: String, val description: String, val runs: (Call) -> Unit, val shortName: String, val type: String) {
    private val prefix = "--$name"
    private val shortPrefix = "-$shortName"

    @Throws(SyntaxError::class)
    fun parse(string: String): T {
        if (string.startsWith("$prefix=")) return parseValue(string.removePrefix("$prefix="))
        if (shortName.isNotEmpty() && string.startsWith("$shortPrefix=")) return parseValue(string.removePrefix("$shortPrefix="))
        return parseValue(string)
    }

    fun matches(string: String): Boolean {
        if (string.startsWith(prefix)) return true
        if (shortName.isNotEmpty() && string.startsWith(shortPrefix)) return true
        return false
    }

    fun prefixOnly(string: String) = string == prefix || string == shortPrefix

    @Throws(SyntaxError::class)
    abstract fun parseValue(string: String): T
}

class StringArgument(name: String, description: String = "", runs: (Call) -> Unit, shortName: String) : Argument<String>(name, description, runs, shortName, "string") {
    override fun parseValue(string: String) = string
}

class IntArgument(name: String, description: String = "", runs: (Call) -> Unit, shortName: String) : Argument<Int>(name, description, runs, shortName, "string") {
    override fun parseValue(string: String): Int {
        try {
            return string.toInt()
        } catch (_: NumberFormatException) {
            throw SyntaxError("$string is not a valid Integer.")
        }
    }
}

class LongArgument(name: String, description: String = "", runs: (Call) -> Unit, shortName: String) : Argument<Long>(name, description, runs, shortName, "string") {
    override fun parseValue(string: String): Long {
        try {
            return string.toLong()
        } catch (_: NumberFormatException) {
            throw SyntaxError("$string is not a valid Long.")
        }
    }
}

class FloatArgument(name: String, description: String = "", runs: (Call) -> Unit, shortName: String) : Argument<Float>(name, description, runs, shortName, "string") {
    override fun parseValue(string: String): Float {
        try {
            return string.toFloat()
        } catch (_: NumberFormatException) {
            throw SyntaxError("$string is not a valid Float.")
        }
    }
}

class DoubleArgument(name: String, description: String = "", runs: (Call) -> Unit, shortName: String) : Argument<Double>(name, description, runs, shortName, "string") {
    override fun parseValue(string: String): Double {
        try {
            return string.toDouble()
        } catch (_: NumberFormatException) {
            throw SyntaxError("$string is not a valid Double.")
        }
    }
}
