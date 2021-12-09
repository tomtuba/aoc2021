package day09

import java.io.File

fun main() {
    val lines = File("data/data09.txt")
        .readLines()
        .map { line -> line.map { it - '0' } }

    val sum = lines.mapIndexed { i, line ->
        line.mapIndexed { j, el ->
            if (i > 0 && lines[i - 1][j] <= el) 0
            else if (i < lines.size - 1 && lines[i + 1][j] <= el) 0
            else if (j > 0 && lines[i][j - 1] <= el) 0
            else if (j < line.size - 1 && lines[i][j + 1] <= el) 0
            else el + 1
        }.sum()
    }.sum()

    println(sum)

    val visited = lines.map {
        it.map { false }.toTypedArray()
    }.toTypedArray()

    fun explore(x: Int, y: Int): Int {
        return if (x < 0 || y < 0 || x >= lines.size || y >= lines[0].size || lines[x][y] == 9 || visited[x][y]) {
            0
        } else {
            visited[x][y] = true
            1 + explore(x - 1, y) + explore(x + 1, y) + explore(x, y + 1) + explore(x, y - 1)
        }
    }

    val basins = lines.asSequence().mapIndexed { i, line ->
        line.mapIndexed { j, _ -> explore(i, j) }.filter { it > 0 }
    }.filter { it.isNotEmpty() }.flatten().sortedBy { -it }.take(3).reduce { a, b -> a * b }

    println(basins)
}