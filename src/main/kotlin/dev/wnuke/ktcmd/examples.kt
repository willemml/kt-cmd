package dev.wnuke.ktcmd

val manager = CommandManager<Message>()

fun main() {
    manager.runCommand(Message("help"))
    println()
    manager.runCommand(Message("help --command help"))
    println()
    manager.runCommand(Message("help --command=help"))
    println()
    manager.runCommand(Message("help -c help"))
    println()
    manager.runCommand(Message("help -c=help"))
}

class Message(message: String) : Call(message) {
    override fun respond(message: String) {
        println(message)
    }
}