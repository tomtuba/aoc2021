package day17

import java.io.File

data class Box constructor(val lowX: Int, val highX: Int, val lowY: Int, val  highY: Int) {
    fun contains(x: Int, y: Int): Boolean {
        return x >= lowX && y <= highX && y >= lowY && y <= highY
    }
    fun past(x: Int, y: Int): Boolean {
        return x > highX || y < lowY
    }
}

fun main() {
    // val fileName = "src/day17/sample.txt"
    val fileName = "data/day17.txt"
    val targetBox = File(fileName).readLines().map { line ->
        val exes = line.substringAfter("x=").substringBefore(",").split("..")
            .map{ it.toInt() }
        val whys = line.substringAfter("y=").split("..")
            .map{ it.toInt() }
        Box(exes[0],exes[1],whys[0],whys[1])
    }.first()

    var maxHeight = 0

    (1..200).forEach{ dxStart ->
        (1..1000).forEach { dyStart ->
            var dx = dxStart
            var dy = dyStart
            var localMax = 0
            var x = 0
            var y = 0
            while (! targetBox.past(x,y)) {
                localMax = localMax.coerceAtLeast(y)
                if (targetBox.contains(x,y)) {
                    maxHeight = maxHeight.coerceAtLeast(localMax)
                    break
                }
                x += dx
                y += dy
                if (dx > 0) dx -= 1
                dy -= 1
            }
        }
    }

    println("maxHeight $maxHeight")

    var tally = 0

    (1..200).forEach{ dxStart ->
        (-120..3000).forEach { dyStart ->
            var dx = dxStart
            var dy = dyStart
            var x = 0
            var y = 0
            while (! targetBox.past(x,y)) {
                if (targetBox.contains(x,y)) {
                    tally ++
                    break
                }
                x += dx
                y += dy
                if (dx > 0) dx -= 1
                dy -= 1
            }
        }
    }

    println("number of shots: $tally")
}