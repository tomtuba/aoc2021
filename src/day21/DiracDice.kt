package day21

val rolls = (1..3).map{ x ->
    (1..3).map{ y->
        (1..3).map{ z -> x + y + z}
    }.flatten()
}.flatten().groupingBy { it }.eachCount()

class GameState constructor(val posOne: Int, val posTwo: Int,
                            val scoreOne: Int, val scoreTwo: Int,
                            val tally: Long) {

    fun isDone(): Boolean {
        return scoreOne >= 21 || scoreTwo >= 21
    }

    fun winnerIsOne(): Boolean {
        return scoreOne >= 21
    }

    fun roll(playerOne: Boolean): List<GameState> {
        return if (playerOne) rolls.map{ thisRoll ->
            var newPos = posOne + thisRoll.key
            while (newPos > 10) newPos -= 10
            GameState(newPos, posTwo, scoreOne + newPos, scoreTwo, tally * thisRoll.value)
        }
        else rolls.map{ thisRoll ->
            var newPos = posTwo + thisRoll.key
            while (newPos > 10) newPos -= 10
            GameState(posOne, newPos, scoreOne, scoreTwo + newPos, tally * thisRoll.value)
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is GameState
                && other.posOne == posOne
                && other.posTwo == posTwo
                && other.scoreOne == scoreOne
                && other.scoreTwo == scoreTwo
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    override fun toString(): String {
        return "[$posOne,$posTwo,$scoreOne,$scoreTwo]"
    }
}


fun main() {
    val stats = mutableListOf(4,0,8,0)

    var die = 0
    var dieCount = 0L
    var playerOne = true

    fun roll(): Int {
        die ++
        if (die > 100) die -= 100
        return die
    }

    fun roll(ct: Int): Int {
        dieCount += 3L
        return (1..ct).sumOf { roll() }
    }

    fun move(start: Int, spaces: Int): Int {
        var result = start + spaces
        while (result > 10) result -= 10
        return result
    }

    fun turn(one: Boolean) {
        if (one) {
            stats[0] = move(stats[0], roll(3))
            stats[1] += stats[0]
        } else {
            stats[2] = move(stats[2], roll(3))
            stats[3] += stats[2]
        }
    }

    while (stats[1] < 1000 && stats[3] < 1000) {
        turn(playerOne)
        playerOne = ! playerOne
    }

    if (stats[1] >= 1000) println(dieCount*stats[3])
    else println(dieCount*stats[1])

    val startingState = GameState(4,8,0,0, 1L)

    var nextTurns = listOf(startingState)

    playerOne = true

    var oneWinners = 0L
    var twoWinners = 0L

    while (nextTurns.isNotEmpty()) {
        val turnResult = nextTurns.map { it.roll(playerOne) }.flatten()

        val subsequent = mutableListOf<GameState>()

        turnResult.forEach{ result ->
            if (result.isDone()) {
                if (result.winnerIsOne()) oneWinners += result.tally
                else twoWinners += result.tally
            } else {
                subsequent.add(result)
            }
        }
        nextTurns = subsequent

        playerOne = ! playerOne
    }

    if (oneWinners > twoWinners) println(oneWinners)
    else println(twoWinners)
}