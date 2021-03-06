package org.jglrxavpok.mcclient.game.world

import java.util.*

class Chunk(val x: Int, val z: Int) {

    val sections = Array(16) { ChunkSection(x, it, z) }

}

class ChunkSection(val x: Int, val y: Int, val z: Int) {

    private val blocks = ShortArray(16*16*16)

    fun getBlockStateID(x: Int, y: Int, z: Int): Short {
        return blocks[index(x, y, z)]
    }

    fun setBlockID(x: Int, y: Int, z: Int, id: Short) {
        blocks[index(x, y, z)] = id
    }

    private inline fun index(x: Int, y: Int, z: Int) = y*16*16+z*16+x

    override fun hashCode(): Int {
        return Objects.hash(x, y, z)
    }
}

data class ChunkCoordinates(val x: Int, val z: Int)