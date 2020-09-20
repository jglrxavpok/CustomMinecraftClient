package org.jglrxavpok.mcclient.rendering.models

import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder
import org.joml.Matrix4fStack

class VariantModel(val variants: Map<String, MinecraftModel>): MinecraftModel {
    override fun fillQuads(matrixStack: Matrix4fStack, meshBuilder: MeshBuilder, state: BlockState, x: Int, y: Int, z: Int) {
        val key = state.toStringKey()
        if(variants.containsKey(key)) {
            variants.getOrDefault(key, MissingModel).fillQuads(matrixStack, meshBuilder, state, x, y, z)
        } else { // use default model, if any
            variants.getOrDefault("", MissingModel).fillQuads(matrixStack, meshBuilder, state, x, y, z)
        }
    }

    override fun textures(): List<Identifier> {
        return variants.values.flatMap { it.textures() }
    }

}