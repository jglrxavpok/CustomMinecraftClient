package org.jglrxavpok.mcclient.rendering.models

import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder

interface MinecraftModel {
    fun fillQuads(meshBuilder: MeshBuilder, state: BlockState, x: Int, y: Int, z: Int)

    fun textures(): List<Identifier>
}