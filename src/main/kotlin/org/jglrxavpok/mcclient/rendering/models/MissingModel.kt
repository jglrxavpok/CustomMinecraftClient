package org.jglrxavpok.mcclient.rendering.models

import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder
import org.joml.Matrix4fStack
import org.joml.Vector3f

object MissingModel: MinecraftModel {
    override fun fillQuads(matrixStack: Matrix4fStack, meshBuilder: MeshBuilder, state: BlockState, x: Int, y: Int, z: Int, tintColor: Vector3f) {
        // TODO
    }

    override fun textures(): List<Identifier> {
        return listOf(Identifier("missingno"))
    }
}