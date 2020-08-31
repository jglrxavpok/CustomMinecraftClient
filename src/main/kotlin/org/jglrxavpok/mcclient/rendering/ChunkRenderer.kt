package org.jglrxavpok.mcclient.rendering

import org.jglrxavpok.mcclient.game.world.Chunk
import org.jglrxavpok.mcclient.game.world.ChunkSection

class ChunkRenderer(camera: Camera) {

    fun init() {
        // TODO: init shaders, etc.
    }

    fun renderChunk(chunk: Chunk) {
        for(section in chunk.sections) {
            renderSection(section)
        }
        // TODO: entities and such
    }

    private fun renderSection(section: ChunkSection) {

    }
}
