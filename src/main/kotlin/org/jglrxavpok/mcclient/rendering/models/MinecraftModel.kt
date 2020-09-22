package org.jglrxavpok.mcclient.rendering.models

import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder
import org.joml.Matrix4fStack
import org.joml.Vector3f

interface MinecraftModel {
    fun fillQuads(matrixStack: Matrix4fStack, meshBuilder: MeshBuilder, state: BlockState, x: Int, y: Int, z: Int, tintColor: Vector3f)

    fun textures(): List<Identifier>
}