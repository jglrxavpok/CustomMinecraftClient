package org.jglrxavpok.mcclient.rendering.models

import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder

class VariantModel(val variants: Map<String, MinecraftModel>): MinecraftModel {
    override fun fillQuads(meshBuilder: MeshBuilder, state: BlockState, x: Int, y: Int, z: Int) {
        variants.getOrDefault(state.toPropertyString(), MissingModel).fillQuads(meshBuilder, state, x, y, z)
    }

    override fun textures(): List<Identifier> {
        return variants.values.flatMap { it.textures() }
    }

}