package day12

import java.io.File

data class Cave constructor(val name: String, val big: Boolean, var count: Int = 0) {
    val paths = mutableListOf<Cave>()
    fun isDouble(): Boolean {
        return ! big && count > 1
    }
    fun isEnd():Boolean {
        return this.name == "end"
    }
}

fun main() {
    // val fileName = "src/day12/sample-small.txt"
    // val fileName = "src/day12/sample-large.txt"
    // val fileName = "src/day12/sample-huge.txt"
    val fileName = "data/day12.txt"

    val caves = mutableMapOf<String, Cave>()

    File(fileName).readLines()
        .forEach{
            it -> val pair = it.split("-").map {
                caves.getOrPut(it) { Cave(it, it.uppercase() == it)}
            }
            pair[0].paths.add(pair[1])
            pair[1].paths.add(pair[0])
        }

    fun explore(cave: Cave, stack: MutableList<Cave> = mutableListOf()): Int {
        if (cave.isEnd()) {
            return 1
        }
        if (stack.isNotEmpty() && cave.name == "start") return 0

        stack.add(cave)
        cave.count ++
        val count = if (stack.filter{it.isDouble()}.distinct().count() < 2 && (cave.big || cave.count <= 2)) {
            cave.paths.sumOf { explore(it, stack) }
        } else 0
        cave.count --
        stack.removeLast()
        return count
    }

    val start = caves.getOrDefault("start", Cave("start", false))

    println(explore(start))
}