package day16

import java.io.File

fun hexToBin(c: Char): String {
    val result = c.digitToInt(16).toString(2)
    return "0000$result".substring(result.length)
}

fun getChunk(packet: StringBuilder, size: Int): Int {
    val chunk = packet.substring(0, size)
    packet.deleteRange(0, size)
    return chunk.toString().toInt(2)
}

fun processPacket(packet: StringBuilder): Long {

    var version = getChunk(packet, 3)
    val type = getChunk(packet, 3)

    if (type == 4) {
        var isLast = false
        var value = 0L
        while (!isLast) {
            isLast = getChunk(packet, 1) == 0
            value *= 16
            value += getChunk(packet, 4)
        }

        return value
    } else {
        val results = mutableListOf<Long>()
        // println("operator type $type")
        val lengthType = getChunk(packet, 1)
        if (lengthType == 0) {
            val length = getChunk(packet, 15)
            val subPacket = StringBuilder(packet.substring(0, length))
            packet.deleteRange(0, length)
            while (subPacket.isNotEmpty()) results.add(processPacket(subPacket))
        } else {
            val subPacketCount = getChunk(packet, 11)
            // println("lengthType 1, count: $subPacketCount ")

            (1..subPacketCount).forEach { it ->
                // println("subpacket $it")
                results.add(processPacket(packet))
            }
        }
        return when (type) {
            0 -> results.sum()
            1 -> results.reduce { a, b -> a * b }
            2 -> results.minOrNull()!!
            3 -> results.maxOrNull()!!
            5 -> if (results[0] > results[1]) 1 else 0
            6 -> if (results[0] < results[1]) 1 else 0
            7 -> if (results[0] == results[1]) 1 else 0
            else -> 0
        }
    }

}

fun main() {
    // val fileName = "src/day16/sample8.txt"
    val fileName = "data/day16.txt"

    val packet = File(fileName).readLines().first()

    listOf("C200B40A82","04005AC33890","880086C3E88112","CE00C43D881120","D8005AC2A8F0","F600BC2D8F","9C005AC2F8F0",
    "9C0141080250320F1802104A08", packet)
        .map{ StringBuilder(it.map { byte -> hexToBin(byte) }.joinToString(""))
        }
        .map{ processPacket(it)}.forEach { println(it) }
}