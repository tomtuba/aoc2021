package day13

import java.io.File

data class Instruction constructor(val horiz: Boolean, val index: Int) {
    val pairIndex = if (horiz) 0 else 1
}

fun main() {
    // val fileName = "src/day13/sample.txt"
    val fileName = "data/day13.txt"

    val points = mutableSetOf<String>()
    val instructions = mutableListOf<Instruction>()

    var pointsDone = false

    File(fileName).readLines().forEach{
        if (it.isEmpty()) pointsDone = true
        else if (! pointsDone) points.add(it)
        else instructions.add(Instruction(it.contains("x="),it.substringAfter("=").toInt()))
    }

    fun makePoint(s: String): List<Int> {
        return s.split(",").map{it.toInt()}
    }

    fun makeString(pair: List<Int>):String {
        return pair.joinToString(",")
    }

    fun foldPoint(point: List<Int>, instruction: Instruction):List<Int> {
        return if (instruction.horiz) listOf(2*instruction.index - point[0],point[1])
        else listOf(point[0],2*instruction.index - point[1])
    }

    fun runInstruction(instruction: Instruction) {
        points.filter {
            makePoint(it)[instruction.pairIndex] > instruction.index
        }.forEach{
            points.remove(it)
            points.add(makeString(foldPoint(makePoint(it), instruction)))
        }
    }

    instructions.forEachIndexed{
        index, it ->
            runInstruction(it)
            if (index == 0) println(points.size)
    }

    println()
    (0..5).forEach{
        row -> (0..38).forEach {
            col -> print(if (points.contains(makeString(listOf(col,row)))) "#" else " ")
        }
        println()
    }
}