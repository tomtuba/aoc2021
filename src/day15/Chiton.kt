package day15

import java.io.File

fun main() {
    val fileName = "src/day15/sample.txt"
    //val fileName = "data/day15.txt"

    val size = 10
    val mult = 5

    val counts = mutableListOf<Int>()
    val grid = mutableListOf<Int>()

    File(fileName).readLines().forEach{ line -> line.forEach { grid.add(it-'0')} }
    (1..size*mult).forEach{_ -> counts.add(0)}

    (0 until mult*size).forEach { row ->
        (0 until mult*size).forEach { col ->
            if (row > 0 || col > 0) {
                val gridIndex = (row % size) * size + (col % size)
                var newValue = grid[gridIndex] + row/size +col/size
                while (newValue > 9) newValue -= 9
                if (row > 0) {
                    counts[col] = newValue + if (col == 0) counts[col] else counts[col].coerceAtMost(counts[col - 1])
                } else {
                    counts[col] = newValue + counts[col-1]
                }
            }
        }
    }

    println(counts.last())
}