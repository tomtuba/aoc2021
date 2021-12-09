package day01

import java.io.File

fun main() {
    println("hey")

    var count: Int = -1
    var last: Int = 0

    File("src/day01/data.txt")
        .readLines()
        .map { it.toInt() }
        .forEach {
            if (it > last) count ++;
            last = it
        }

    println(count)
}