package org.jglrxavpok.mcclient.rendering.models

import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder
import org.joml.Matrix4fStack
import org.joml.Vector3f

class VariantModel(val variants: Map<String, MinecraftModel>): MinecraftModel {
    override fun fillQuads(matrixStack: Matrix4fStack, meshBuilder: MeshBuilder, state: BlockState, x: Int, y: Int, z: Int, tintColor: Vector3f) {
        val key = state.toStringKey()
        if(variants.containsKey(key)) {
            variants.getOrDefault(key, MissingModel).fillQuads(matrixStack, meshBuilder, state, x, y, z, tintColor)
        } else { // use default model, if any
            variants.getOrDefault("", MissingModel).fillQuads(matrixStack, meshBuilder, state, x, y, z, tintColor)
        }
    }

    override fun textures(): List<Identifier> {
        return variants.values.flatMap { it.textures() }
    }

}