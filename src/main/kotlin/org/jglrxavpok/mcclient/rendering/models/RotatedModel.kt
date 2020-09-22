package org.jglrxavpok.mcclient.rendering.models

import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder
import org.joml.Matrix4fStack
import org.joml.Vector3f

class RotatedModel(val xAngle: Float, val yAngle: Float, val baseModel: JsonModel) : MinecraftModel {
    override fun fillQuads(
            matrixStack: Matrix4fStack,
            meshBuilder: MeshBuilder,
            state: BlockState,
            x: Int,
            y: Int,
            z: Int,
            tintColor: Vector3f
    ) {
        matrixStack.pushMatrix()
        matrixStack.translate(0.5f, 0.5f, 0.5f)
        matrixStack.rotateY(yAngle)
        matrixStack.rotateX(xAngle)
        matrixStack.translate(-0.5f, -0.5f, -0.5f)
        baseModel.fillQuads(matrixStack, meshBuilder, state, x, y, z, tintColor)
        matrixStack.popMatrix()
    }

    override fun textures() = baseModel.textures()
}
