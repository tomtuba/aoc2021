package day14

import java.io.File
import java.lang.Integer.min

fun main() {
    // val fileName = "src/day14/sample.txt"
    val fileName = "data/day14.txt"

    var pairs = mutableMapOf<String, Long>()
    val instructions = mutableMapOf<String, String>()

    File(fileName).readLines().forEach{ line ->
        if (pairs.isEmpty()) {
            (1..line.length).forEach { index ->
                val key = line.substring(index-1, min(line.length, index+1))
                pairs[key] = 1L + pairs.getOrDefault(key, 0L)
            }
        } else if (line.isNotEmpty()) {
            instructions[line.substring(0,2)] = line.substring(6)
        }
    }

    (1..40).forEach { _ ->
        val newMap = mutableMapOf<String, Long>()
        pairs.filter{! instructions.containsKey(it.key)}.forEach{newMap[it.key] = it.value}
        instructions.forEach {
            if (pairs.contains(it.key)) {
                val count = pairs[it.key]!!
                val keys = listOf(it.key.substring(0, 1) + it.value, it.value + it.key.substring(1))
                keys.forEach { key ->
                    newMap[key] = count + newMap.getOrDefault(key, 0L)
                }
            }
        }
        pairs = newMap
    }

    val counts = mutableMapOf<String, Long>()
    pairs.forEach{
        val key = it.key.substring(0,1)
        counts[key] = it.value + counts.getOrDefault(key, 0L)
    }
    val tallies = counts.values.sorted()
    println(tallies.last() - tallies.first())
}