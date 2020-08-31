package org.jglrxavpok.mcclient.game.world

class World {

    val chunks = HashMap<ChunkCoordinates, Chunk>()

    fun getChunk(x: Int, z: Int): Chunk? {
        return chunks[ChunkCoordinates(x, z)]
    }

    fun getOrCreateChunk(x: Int, z: Int): Chunk {
        return chunks.computeIfAbsent(ChunkCoordinates(x, z)) { Chunk(x, z) }
    }
}