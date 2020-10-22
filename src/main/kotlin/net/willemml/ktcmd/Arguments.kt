package net.willemml.ktcmd

import kotlin.reflect.KClass

class RuntimeCommandSyntaxError(problem: String) : RuntimeException(problem)

/**
 * Abstract argument, [T] is the type of the argument, [U] is the type of call the argument's [runs] block is called with
 */
abstract class Argument<T, U : Call>(
    name: String,
    val description: String,
    /**
     * What to run when this argument is executed
     */
    val runs: U.(T) -> Unit,
    /**
     * Short name to use, ideally 1-2 characters so that it is easier to use this argument in a CLI
     */
    val shortName: String,
    /**
     * Default value for this argument, usually used when the argument is optional and was not given
     */
    val default: T,
    val type: KClass<*>
) {
    val prefix = "--$name"
    val shortPrefix = "-$shortName"

    /**
     * Parses [string] to get the value with type [T]
     * @return The parsed value of [string] as [T]
     * @throws RuntimeCommandSyntaxError when the argument value given by the user is of the wrong type
     */
    @Throws(RuntimeCommandSyntaxError::class)
    fun parse(string: String): T {
        if (string.startsWith("$prefix=")) return parseValue(string.removePrefix("$prefix="))
        if (shortName.isNotEmpty() && string.startsWith("$shortPrefix=")) return parseValue(string.removePrefix("$shortPrefix="))
        return parseValue(string)
    }

    /**
     * @return true if [string] matches either of [prefix] and [shortPrefix] else false
     */
    fun matches(string: String): Boolean {
        if (string.startsWith(prefix)) return true
        if (shortName.isNotEmpty() && string.startsWith(shortPrefix)) return true
        return false
    }

    /**
     * @return if [string] is equal to [prefix] or [shortPrefix]
     */
    fun prefixOnly(string: String) = string == prefix || string == shortPrefix

    @Throws(RuntimeCommandSyntaxError::class)
    internal abstract fun parseValue(string: String): T
}

/**
 * Argument with type String
 */
class StringArgument<T : Call>(name: String, description: String = "", default: String, runs: T.(String) -> Unit, shortName: String) :
    Argument<String, T>(name, description, runs, shortName, default, String::class) {
    override fun parseValue(string: String) = string
}

/**
 * Argument with type Boolean
 */
class BooleanArgument<T : Call>(name: String, description: String = "", default: Boolean, runs: T.(Boolean) -> Unit, shortName: String) :
    Argument<Boolean, T>(name, description, runs, shortName, default, Boolean::class) {
    override fun parseValue(string: String): Boolean = string.toBoolean()
}

/**
 * Argument with type Int
 */
class IntArgument<T : Call>(name: String, description: String = "", default: Int, runs: T.(Int) -> Unit, shortName: String) :
    Argument<Int, T>(name, description, runs, shortName, default, Int::class) {
    override fun parseValue(string: String): Int {
        try {
            return string.toInt()
        } catch (_: NumberFormatException) {
            throw RuntimeCommandSyntaxError("$string is not a valid Integer.")
        }
    }
}

/**
 * Argument with type Long
 */
class LongArgument<T : Call>(name: String, description: String = "", default: Long, runs: T.(Long) -> Unit, shortName: String) :
    Argument<Long, T>(name, description, runs, shortName, default, Long::class) {
    override fun parseValue(string: String): Long {
        try {
            return string.toLong()
        } catch (_: NumberFormatException) {
            throw RuntimeCommandSyntaxError("$string is not a valid Long.")
        }
    }
}

/**
 * Argument with type Float
 */
class FloatArgument<T : Call>(name: String, description: String = "", default: Float, runs: T.(Float) -> Unit, shortName: String) :
    Argument<Float, T>(name, description, runs, shortName, default, Float::class) {
    override fun parseValue(string: String): Float {
        try {
            return string.toFloat()
        } catch (_: NumberFormatException) {
            throw RuntimeCommandSyntaxError("$string is not a valid Float.")
        }
    }
}

/**
 * Argument with type Double
 */
class DoubleArgument<T : Call>(name: String, description: String = "", default: Double, runs: T.(Double) -> Unit, shortName: String) :
    Argument<Double, T>(name, description, runs, shortName, default, Double::class) {
    override fun parseValue(string: String): Double {
        try {
            return string.toDouble()
        } catch (_: NumberFormatException) {
            throw RuntimeCommandSyntaxError("$string is not a valid Double.")
        }
    }
}
