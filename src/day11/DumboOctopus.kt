package day11

import java.io.File

data class Cell constructor(var score: Int, val x: Int, val y: Int){
    val dirs = (-1..1).map{ i -> (-1..1).map{ j -> intArrayOf(i,j)}}.flatten()
        .filter{it[0] != 0 || it[1] != 0}

    fun neighbors(grid: List<List<Cell>>): List<Cell> {
        return dirs.map{ dir -> intArrayOf(dir[0]+x,dir[1]+y)}
            .filter{pair -> pair[0] >=0 && pair[0] < grid.size && pair[1] >= 0 && pair[1] < grid[0].size}
            .map{pair -> grid[pair[0]][pair[1]]}
    }

    fun increment(): Int {
        score += 1
        return score
    }
}

fun main() {
    val fileName = "src/day11/sample.txt"
    //val fileName = "data/day11.txt"
    val grid: List<List<Cell>> = File(fileName)
        .readLines()
        .map{ it.map{ it - '0' } }
        .mapIndexed{ i, row -> row.mapIndexed{j, el -> Cell(el, i,j)}}


    var flashes = 0

    fun turn(): Boolean {
        val blinkers = ArrayDeque<Cell>()
        grid.forEach{row -> row.forEach{cell ->
            if (cell.increment() == 10) {
                blinkers.add(cell)
                flashes += 1
            }
        }}
        while (blinkers.isNotEmpty()) {
            blinkers.removeFirst().neighbors(grid).forEach{neighbor -> if(neighbor.increment() == 10) {
                blinkers.add(neighbor)
                flashes += 1
            }}
        }

        grid.forEach{row -> row.forEach{cell ->
            if (cell.score > 9) cell.score = 0
        }}

        return grid.flatten().all{ it.score == 0}
    }

    (1..100).forEach{turn()}
    println(flashes)

    var tally = 101
    while (! turn()) tally += 1
    println(tally)
}