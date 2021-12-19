package day19

import java.io.File
import kotlin.math.abs

data class Hit constructor(val x: Int, val y: Int, val z: Int) {
    fun manhattanDist(): Int {
        return abs(x) + abs(y) + abs(z)
    }
    override fun equals(other: Any?): Boolean {
        return other is Hit && other.x == x && other.y == y && other.z == z
    }

    override fun toString(): String {
        return "[$x,$y,$z]"
    }
}

class LocalScanner {
    val hits = mutableListOf<Hit>()
    fun transform(t: Transform): LocalScanner {
        val result = LocalScanner()
        hits.forEach { hit -> result.hits.add(t(hit)) }
        return result
    }

    override fun toString(): String {
        return hits.joinToString(",")
    }
}

fun maxMatches(one: LocalScanner, two: LocalScanner): Pair<Hit, Int> {
    val topCount = one.hits.map { oneHit ->
        two.hits.map { twoHit ->
            Hit(oneHit.x - twoHit.x, oneHit.y - twoHit.y, oneHit.z - twoHit.z)
        }
    }.flatten().groupingBy { it }.eachCount().entries.maxByOrNull { it.value }
    return Pair(topCount!!.key, topCount.value)
}

fun buildTwists(): List<Transform> {
    val xTurn = { hit: Hit -> Hit(hit.x, hit.z, -hit.y) }
    val yTurn = { hit: Hit -> Hit(hit.z, hit.y, -hit.x) }
    val zTurn = { hit: Hit -> Hit(hit.y, -hit.x, hit.z) }

    return listOf(
        { hit -> hit }, xTurn, yTurn, zTurn,
        { hit -> xTurn(xTurn(hit)) },
        { hit -> yTurn(yTurn(hit)) },
        { hit -> zTurn(zTurn(hit)) },
        { hit -> yTurn(xTurn(hit)) },
        { hit -> xTurn(yTurn(hit)) },
        { hit -> xTurn(zTurn(hit)) },
        { hit -> zTurn(yTurn(hit)) },
        { hit -> xTurn(xTurn(xTurn(hit))) },
        { hit -> xTurn(xTurn(yTurn(hit))) },
        { hit -> xTurn(xTurn(zTurn(hit))) },
        { hit -> xTurn(yTurn(xTurn(hit))) },
        { hit -> xTurn(yTurn(yTurn(hit))) },
        { hit -> xTurn(zTurn(zTurn(hit))) },
        { hit -> yTurn(xTurn(xTurn(hit))) },
        { hit -> yTurn(yTurn(yTurn(hit))) },
        { hit -> zTurn(zTurn(zTurn(hit))) },
        { hit -> xTurn(xTurn(xTurn(yTurn(hit)))) },
        { hit -> xTurn(xTurn(yTurn(xTurn(hit)))) },
        { hit -> xTurn(yTurn(xTurn(xTurn(hit)))) },
        { hit -> xTurn(yTurn(yTurn(yTurn(hit)))) },
    )
}

typealias Transform = (Hit) -> Hit

fun main() {
    // val fileName = "src/day19/sample.txt"
    val fileName = "data/day19.txt"

    val scanners = mutableListOf<LocalScanner>()
    var scanner = LocalScanner()
    File(fileName).readLines().forEach {
        if (it.contains("scanner")) {
            scanner = LocalScanner()
            scanners.add(scanner)
        } else if (it.isNotEmpty()) {
            val arr = it.split(",").map { pos -> pos.toInt() }
            scanner.hits.add(Hit(arr[0], arr[1], arr[2]))
        }
    }

    val twists = buildTwists()

    val matches = mutableListOf(scanners.removeFirst())

    val distances = mutableListOf(Hit(0,0,0))

    while (scanners.isNotEmpty()) {
        candSearch@ for (candidate in scanners) {
            val transformations = twists.map { twist -> candidate.transform(twist) }

            for (localScanner in matches) {
                for (transformation in transformations) {
                    val match = maxMatches(localScanner, transformation)

                    if (match.second > 11) {
                        val newMatch = LocalScanner()
                        val translate = match.first
                        distances.add(translate)
                        newMatch.hits.addAll(transformation.hits.map { hit ->
                            Hit(hit.x + translate.x, hit.y + translate.y, hit.z + translate.z)
                        })
                        matches.add(newMatch)
                        scanners.remove(candidate)
                        break@candSearch
                    }
                }
            }
        }
    }

    val uniqueHits = mutableSetOf<Hit>()
    matches.forEach { match -> uniqueHits.addAll(match.hits) }

    println(uniqueHits.size)

    var maxManhattanDist = 0

    for (one in distances) {
        for (two in distances) {
            maxManhattanDist = maxManhattanDist.coerceAtLeast(Hit(one.x-two.x, one.y-two.y, one.z-two.z).manhattanDist())
        }
    }
    println("manhattanDist: $maxManhattanDist")
}