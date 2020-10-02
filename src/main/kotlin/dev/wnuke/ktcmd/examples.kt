package dev.wnuke.ktcmd

val manager = CommandManager<Message>()

fun main() {
    val testCommandZero = Command<Message>("testZero", "Test for commands with no arguments.", arrayListOf("0")) {
        println("$name: $description")
        println("Test zero success!")
    }
    val testCommandOne = Command<Message>("testOne", "Test for commands with optional arguments only.", arrayListOf("t")) {
        println("$name: $description")
        getOptionalArgument<String>("str")?.let { println("string test: $it") }
        getOptionalArgument<Int>("int")?.let { println("int test: $it") }
        getOptionalArgument<Long>("long")?.let { println("long test: $it") }
        getOptionalArgument<Float>("float")?.let { println("float test: $it") }
        getOptionalArgument<Double>("double")?.let { println("double test: $it") }
        try {
            println("int as string: ${getOptionalArgument<String>("int")?: "null"}")
        } catch (e: IllegalArgumentException) {
            println("Invalid getOptionalArgument type test success")
        }
        println("Test one success!")
    }.apply {
        string("str", false, "An optional string argument for testing", "s")
        integer("int", false, "An optional integer argument for testing", "i")
        long("long", false, "An optional long argument for testing", "l")
        float("float", false, "An optional float argument for testing", "f")
        double("double", false, "An optional double argument for testing", "d")
        println("All arguments registered.")
    }
    val testCommandTwo = Command<Message>("testTwo", "Test for commands with required arguments, also tests argument types.") {
        println("$name: $description")
        println("string: ${getArgument<String>("str")}")
        println("int: ${getArgument<Int>("int")}")
        println("long: ${getArgument<Long>("long")}")
        println("float: ${getArgument<Float>("float")}")
        println("double: ${getArgument<Double>("double")}")
        try {
            println("float as int: ${getArgument<Float>("float")}")
        } catch (e: IllegalArgumentException) {
            println("Invalid getArgument type test success")
        }
        println("Test two success!")
    }.apply {
        string("str", true, "A required string argument for testing", "s")
        integer("int", true, "A required integer argument for testing", "i")
        long("long", true, "A required long argument for testing", "l")
        float("float", true, "A required float argument for testing", "f")
        double("double", true, "A required double argument for testing", "d")

        try {
            string("str", true, "s")
        } catch (e: IllegalArgumentException) {
            println("Duplicate argument error check complete.")
        }
        println("All arguments registered.")
    }

    val testCommandThree = Command<Message>("testThree", "Test for commands with required and optional arguments, used for help test.") {}.apply {
        string("optstr", false, "An optional string argument for testing", "os")
        integer("optint", false, "An optional integer argument for testing", "oi")
        long("optlong", false, "An optional long argument for testing", "ol")
        float("optfloat", false, "An optional float argument for testing", "of")
        double("optdouble", false, "An optional double argument for testing", "od")
        string("reqstr", true, "A required string argument for testing", "rs")
        integer("reqint", true, "A required integer argument for testing", "ri")
        long("reqlong", true, "A required long argument for testing", "rl")
        float("reqfloat", true, "A required float argument for testing", "rf")
        double("reqdouble", true, "A required double argument for testing", "rd")
    }

    manager.loadCommands(arrayOf(testCommandZero, testCommandOne, testCommandTwo, testCommandThree))
    println("\n====== Testing argument parsing ======\nAliases:\n> 0")
    manager.runCommand(Message("0"))
    println("\n> t")
    manager.runCommand(Message("t"))
    println("\nArguments, optional:\nnone > testZero")
    manager.runCommand(Message("testZero"))
    println("\nnone > testOne")
    manager.runCommand(Message("testOne"))
    println("\nstring with spaces > testOne")
    manager.runCommand(Message("testOne --str \"Test, with spaces!\""))
    println("\nsome > testOne")
    manager.runCommand(Message("testOne --str test -i 42"))
    println("\nall > testOne --str test -i 42 -l=64 --float=3.2 --double 57.33")
    manager.runCommand(Message("testOne --str test -i 42 -l=64 --float=3.2 --double 57.33"))
    println("\nArguments, required:\nnone > testTwo")
    manager.runCommand(Message("testTwo"))
    println("\nsome > testTwo -s test")
    manager.runCommand(Message("testOne --str test -i 42"))
    println("\nall > testTwo --str test -i 42 -l=64 --float=3.2 --double 57.33")
    manager.runCommand(Message("testTwo --str test -i 42 -l=64 --float=3.2 --double 57.33"))
    println("\nWrong type\n> testOne -i test")
    manager.runCommand(Message(" testOne -i test"))
    println("\n\n====== Testing help command ======\nWithout arguments:\n> help")
    manager.runCommand(Message("help"))
    println("\nARGUMENT FORMATS:\n> help --command testZero")
    manager.runCommand(Message("help --command testZero"))
    println("\n> help --command=testOne")
    manager.runCommand(Message("help --command=testOne"))
    println("\n> help -c testTwo")
    manager.runCommand(Message("help -c testTwo"))
    println("\n> help -c=testThree")
    manager.runCommand(Message("help -c=testThree"))
    println("\nWith non existent command as argument:\n> help --command invalid")
    manager.runCommand(Message("help --command invalid"))
}

class Message(message: String) : Call(message) {
    override fun respond(message: String) {
        println(message)
    }
}