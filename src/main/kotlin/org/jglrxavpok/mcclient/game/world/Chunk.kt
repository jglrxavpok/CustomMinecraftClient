package org.jglrxavpok.mcclient.game.world

class Chunk(val x: Int, val z: Int) {

    val sections = Array(16) { ChunkSection(it) }

}

class ChunkSection(val y: Int) {

    private val blocks = IntArray(16*16*16)

    fun getBlockID(x: Int, y: Int, z: Int): Int {
        return blocks[index(x, y, z)]
    }

    fun setBlockID(x: Int, y: Int, z: Int, id: Int) {
        blocks[index(x, y, z)] = id
    }

    private inline fun index(x: Int, y: Int, z: Int) = y*16*16+z*16+x
}

data class ChunkCoordinates(val x: Int, val z: Int)