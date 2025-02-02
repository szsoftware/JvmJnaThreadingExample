package org.example

import uniffi.example_library.libMain
import java.util.Scanner

fun main() {
    println("Hello in JavaWorld Thread Example:")
    println("Current ProcessId: ${ProcessHandle.current().pid()}")

    jvmRegisteredThreads()

    println()
    println("I'm calling now libMain() in native library and leave JavaWorld for a moment.")
    libMain()
    println()
    println("We are back in the JavaWorld.")
    jvmRegisteredThreads()
    println()
    println("All done. I'm loitering in an infinite loop now for testing purposes.")
    while (true) { /* ignored */ }
}

fun jvmRegisteredThreads() {
    val currentJavaThreads = Thread.getAllStackTraces().keys.sortedBy { it.id }
    println("${currentJavaThreads.size} JVM registered threads:")
    currentJavaThreads.forEach { thread ->
        println("Java-ID: ${thread.id}, Thread-Name: '${thread.name}'")
    }
}