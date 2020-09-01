package org.jglrxavpok.mcclient.game.world

import java.util.concurrent.ConcurrentHashMap

class World {

    val chunks = ConcurrentHashMap<ChunkCoordinates, Chunk>()

    fun getChunk(x: Int, z: Int): Chunk? {
        return chunks[ChunkCoordinates(x, z)]
    }

    fun getOrCreateChunk(x: Int, z: Int): Chunk {
        return chunks.computeIfAbsent(ChunkCoordinates(x, z)) { Chunk(x, z) }
    }
}