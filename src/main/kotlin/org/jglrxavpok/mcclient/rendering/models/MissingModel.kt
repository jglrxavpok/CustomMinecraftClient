package org.jglrxavpok.mcclient.rendering.models

import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder

object MissingModel: MinecraftModel {
    override fun fillQuads(meshBuilder: MeshBuilder, state: BlockState, x: Int, y: Int, z: Int) {
        TODO("fill with quads")
    }

    override fun textures(): List<Identifier> {
        return listOf(Identifier("missingno"))
    }
}