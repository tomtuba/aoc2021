package day20

import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Pixel constructor(val x: Int, val y: Int) {
    override fun equals(other: Any?): Boolean {
        return other is Pixel && other.x == x && other.y == y
    }

    override fun toString(): String {
        return "[$x,$y]"
    }

    override fun hashCode(): Int {
        return x * 10000000 + y
    }
}

fun xRange(ls: Set<Pixel>): Pair<Int, Int> {
    val min = ls.map { it.x }.minOrNull()
    val max = ls.map { it.x }.maxOrNull()
    return Pair(min!!, max!!)
}

fun yRange(ls: Set<Pixel>): Pair<Int, Int> {
    val min = ls.map { it.y }.minOrNull()
    val max = ls.map { it.y }.maxOrNull()
    return Pair(min!!, max!!)
}

fun iterateOdd(ls: Set<Pixel>, algorithm: List<Boolean>): MutableSet<Pixel> {
    val rows = yRange(ls)
    val cols = xRange(ls)

    val result = mutableSetOf<Pixel>()

    (rows.first-1..rows.second+1).forEach{row ->
        (cols.first-1..cols.second+1).forEach { col ->
            var key = 0
            (row-1..row+1).forEach { smRow ->
                (col-1..col+1).forEach { smCol ->
                    key *= 2
                    if (ls.contains(Pixel(smCol,smRow))) {
                        key ++
                    }
                }
            }
            if (! algorithm[key]) result.add(Pixel(col, row))
        }
    }
    return result
}

fun iterateEven(ls: Set<Pixel>, algorithm: List<Boolean>): MutableSet<Pixel> {
    val rows = yRange(ls)
    val cols = xRange(ls)

    val result = mutableSetOf<Pixel>()

    (rows.first-1..rows.second+1).forEach{row ->
        (cols.first-1..cols.second+1).forEach { col ->
            var key = 0
            (row-1..row+1).forEach { smRow ->
                (col-1..col+1).forEach { smCol ->
                    key *= 2
                    if (! ls.contains(Pixel(smCol,smRow))) {
                        key ++
                    }
                }
            }
            if (algorithm[key]) result.add(Pixel(col, row))
        }
    }
    return result
}

@OptIn(ExperimentalTime::class)
fun main() {
    //val fileName = "src/day20/sample.txt"
    val fileName = "data/day20.txt"

    var pixels = mutableSetOf<Pixel>()
    val algorithm = mutableListOf<Boolean>()
    var row = 0
    
    File(fileName).readLines().forEach{ line ->
        if (algorithm.isEmpty()) {
            algorithm.addAll(line.map{it == '#'})
        } else if (line.isNotEmpty()) {
            line.forEachIndexed { col, c -> if (c == '#') pixels.add(Pixel(col, row)) }
            row ++
        }
    }

    val elapsed = measureTime {
        (1..25).forEach { _ ->
            pixels = iterateEven(iterateOdd(pixels, algorithm), algorithm)
        }

        println(pixels.size)
    }

    println("time: $elapsed")

}