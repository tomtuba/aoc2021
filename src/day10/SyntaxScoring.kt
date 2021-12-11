package day10

import java.io.File

fun getCorruptionScore(s: String): Int {
    val stack = ArrayDeque<Char>()
    for (char in s) {
        if ("<{([".contains(char)) {
            stack.addLast(char)
        } else {
            if (stack.isNotEmpty()) {
                val partner = stack.removeLast()

                if (partner != '<' && char == '>') return 25137
                else if (partner != '(' && char == ')')  return 3
                else if (partner != '{' && char == '}') return 1197
                else if (partner != '[' && char == ']') return 57
            }
        }
    }

    return 0
}

fun getRemaining(s: String): Collection<Char> {
    val stack = ArrayDeque<Char>()
    for (char in s) {
        if ("<{([".contains(char)) {
            stack.addLast(char)
        } else {
            if (stack.isEmpty()) {
                return stack.reversed()
            }
            stack.removeLast()
        }
    }

    return stack.reversed()
}

fun score(arr: Collection<Char>): Long {
    return arr.map {
        when {
            (it == '(') -> 1L
            (it == '[') -> 2L
            (it == '{') -> 3L
            (it == '<') -> 4L
            else -> 0L
        }
    }.reduce{
        a,b -> a*5L + b
    }
}

fun main() {

    // val fileName: String = "src/day10/sample.txt"
    val fileName = "data/day10.txt"
    val lines = File(fileName)
        .readLines()

    val corruptionScore = lines.map { getCorruptionScore(it) }

    println(corruptionScore.sum())

    val result = lines.filter{ getCorruptionScore(it) == 0 }
        .map{ getRemaining(it) }

        val score = result.map{ score(it) }
        .sorted()

    println(score[(score.size-1)/2])
}