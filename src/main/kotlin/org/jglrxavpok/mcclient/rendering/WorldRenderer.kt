package org.jglrxavpok.mcclient.rendering

import org.jglrxavpok.mcclient.game.world.World

object WorldRenderer {

    val camera = Camera()
    val chunkRenderer = ChunkRenderer(camera)

    fun init() {
        chunkRenderer.init()
    }

    fun render(world: World) {
        // TODO: optimize
        for(pass in RenderPass.values()) {
            pass.use {
                for(chunk in world.chunks.values) {
                    chunkRenderer.renderChunk(chunk, pass)
                }
            }
        }
    }
}
