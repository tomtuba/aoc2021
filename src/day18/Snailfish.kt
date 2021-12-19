package day18

import java.io.File
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

open class Element() {
    fun add(other: Element): Element {
        return SnailfishPair(this, other)
    }
    open fun canExplode(depth: Int): Boolean {
        return false
    }
    open fun magnitude(): Long {
        return 0
    }
    open fun canSplit(): Boolean {
        return false
    }
    open fun finalize(): Element {
        return this
    }
}

class Value(var value: Long): Element() {
    override fun canSplit(): Boolean {
        return value >= 10
    }
    fun split(): SnailfishPair {
        return SnailfishPair(Value(floor(value/2.0).toLong()), Value(ceil(value/2.0).toLong()))
    }

    override fun magnitude(): Long {
        return value
    }

    override fun toString(): String {
        return value.toString()
    }
}

open class SnailfishPair(val first: Element, val second: Element): Element() {
    override fun canExplode(depth: Int): Boolean {
        return depth >= 4 && first is Value && second is Value
    }
    fun explode(): Element {
        return ExplodedSnailfishPair(first, second)
    }

    override fun magnitude(): Long {
        return first.magnitude() * 3 + second.magnitude() * 2
    }

    override fun toString(): String {
        return "[$first,$second]"
    }

    override fun finalize(): Element {
        return SnailfishPair(first.finalize(), second.finalize())
    }
}

class ExplodedSnailfishPair(first: Element, second: Element): SnailfishPair(first, second) {
    override fun finalize(): Element {
        return Value(0)
    }
}

fun build(s: String): Element {
    if (s[0] == '[') {
        var level = 0
        var wordOne = ""
        var wordTwo = ""
        var index = 1
        while (wordOne.isEmpty()) {
            if (s[index] == '[') level ++
            else if (s[index] == ']') level --
            else if (level == 0 && s[index] == ',') {
                wordOne = s.substring(1,index)
                wordTwo = s.substring(index+1, s.length-1)
            }
            index ++
        }
        return SnailfishPair(build(wordOne), build(wordTwo))
    } else {
        return Value(s.toLong())
    }
}

fun loopExplode(tree: Element): Element {
    var shouldContinue = true
    var top = tree

    while (shouldContinue) {
        val result = tryExplode(top)
        val flat = mutableListOf<Element>()
        flatten(result.first, flat)
        if (flat.filterIsInstance<ExplodedSnailfishPair>().isNotEmpty()) {
            val before = mutableListOf<Value>()
            var pair = Element()
            val after = mutableListOf<Value>()

            flat.forEach {
                if (pair is ExplodedSnailfishPair) {
                    after.add((it as Value))
                } else if (it is ExplodedSnailfishPair) {
                    pair = it
                } else {
                    before.add((it as Value))
                }
            }

            if (before.isNotEmpty()) {
                before.last().value += ((pair as ExplodedSnailfishPair).first as Value).value
            }
            if (after.isNotEmpty()) {
                after.first().value += ((pair as ExplodedSnailfishPair).second as Value).value
            }
        }
        top = result.first.finalize()
        shouldContinue = result.second
    }
    return top
}

fun tryExplode(e: Element, level: Int = 0): Pair<Element, Boolean>  {
    if (e.canExplode(level)) {
        return Pair((e as SnailfishPair).explode(), true)
    } else if (e is SnailfishPair) {
        val firstEl = tryExplode(e.first, level + 1)
        if(firstEl.second) return Pair(SnailfishPair(firstEl.first, e.second), true)
        val secondEl = tryExplode(e.second, level + 1)
        if (secondEl.second) return Pair(SnailfishPair(e.first, secondEl.first), true)
    }
    return Pair(e,false)
}

fun trySplit(e: Element): Pair<Element, Boolean> {
    if (e.canSplit()) {
        return Pair((e as Value).split(), true)
    } else if (e is SnailfishPair) {
        val firstEl = trySplit(e.first)
        if(firstEl.second) return Pair(SnailfishPair(firstEl.first, e.second), true)
        val secondEl = trySplit(e.second)
        if (secondEl.second) return Pair(SnailfishPair(e.first, secondEl.first), true)
    }
    return Pair(e,false)
}

fun flatten(e: Element, result: MutableList<Element>) {
    if (e is Value || e is ExplodedSnailfishPair) {
        result.add(e)
    } else {
        val pair = e as SnailfishPair
        flatten(pair.first, result)
        flatten(pair.second, result)
    }
}

fun process(tree: Element): Element {
    var shouldContinue = true
    var top = tree

    while (shouldContinue) {
        top = loopExplode(top)
        val splitAttempt = trySplit(top)
        top = splitAttempt.first
        shouldContinue = splitAttempt.second
    }

    return top
}

@OptIn(ExperimentalTime::class)
fun main() {

    val fileName = "data/day18.txt"

    val lines = File(fileName).readLines()

    val elapsedOne = measureTime {
        val nums = lines.map{build(it)}

        val result = nums.reduce{ a,b ->
            val result = process(a.add(b))
            result
        }

        println(result.magnitude())
    }

    println("elapsedOne $elapsedOne")

    val elapsedTwo = measureTime {
        var max = 0L

        (0 until 100).forEach{ aIndex->
            (0 until 100).forEach { bIndex ->
                if (aIndex != bIndex) {
                    val a = build(lines[aIndex])
                    val b = build(lines[bIndex])
                    max = max.coerceAtLeast(process(a.add(b)).magnitude())
                }
            }
        }

        println(max)
    }

    println("elapsedTwo: $elapsedTwo")

}