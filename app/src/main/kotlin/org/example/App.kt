package org.example

import com.sun.jna.Library
import com.sun.jna.Native
import uniffi.example_library.libMain
import java.util.Scanner

// Define an interface to access the native libc function
interface CLibrary : Library {
    fun gettid(): Int // Native system call for getting the thread ID
}

// Load libc using JNA
//val libc: CLibrary = Native.load("c", CLibrary::class.java)

fun main() {
    val systemPid = ProcessHandle.current().pid()
    //val systemTid = libc.gettid()
    println("Hello in JavaWorld Thread Example:")
    println("Current ProcessId: $systemPid")
    //println("Current Thread ID: $systemTid")

    jvmRegisteredThreads()

    while (true) { /* ignored */ }

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