package day01

import java.io.File

fun main() {
    println("hey")

    var count: Int = -1
    var last = 0

    File("src/day01/data09.txt")
        .readLines()
        .map { it.toInt() }
        .forEach {
            if (it > last) count ++
            last = it
        }

    println(count)
}