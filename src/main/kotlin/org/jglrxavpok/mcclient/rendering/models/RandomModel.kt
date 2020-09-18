package org.jglrxavpok.mcclient.rendering.models

import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder
import org.joml.Matrix4fStack
import java.util.*
import kotlin.math.absoluteValue

class RandomModel(val elements: List<MinecraftModel>): MinecraftModel {

    private fun hash(x: Int, y: Int, z: Int): Int {
        // TODO: better hash/noise
        return Objects.hash(x, y, z).absoluteValue % elements.size
    }

    override fun fillQuads(matrixStack: Matrix4fStack, meshBuilder: MeshBuilder, state: BlockState, x: Int, y: Int, z: Int) {
        val index = hash(x, y, z)
        elements[index].fillQuads(matrixStack, meshBuilder, state, x, y, z)
    }

    override fun textures(): List<Identifier> {
        return elements.flatMap(MinecraftModel::textures)
    }
}