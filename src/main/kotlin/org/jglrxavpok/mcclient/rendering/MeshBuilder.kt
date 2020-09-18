package org.jglrxavpok.mcclient.rendering

import org.joml.Matrix4fStack
import org.joml.Vector3f

class MeshBuilder {

    private val vertexData = mutableListOf<Float>()
    private val indexData = mutableListOf<Int>()
    private var vertexCount: Int = 0

    fun vertex(matrixStack: Matrix4fStack, x: Float, y: Float, z: Float, u: Float, v: Float): MeshBuilder {
        val dest by lazy { Vector3f() }
        matrixStack.transformPosition(x, y, z, dest)
        return vertex(dest.x, dest.y, dest.z, u, v)
    }

    fun vertex(x: Float, y: Float, z: Float, u: Float, v: Float): MeshBuilder {
        vertexData += x
        vertexData += y
        vertexData += z
        vertexData += u
        vertexData += v
        vertexCount++
        if(vertexCount % 4 == 0) {
            // 0 1 2
            // 2 3 0
            indexData += vertexCount-4
            indexData += vertexCount-3
            indexData += vertexCount-2
            indexData += vertexCount-2
            indexData += vertexCount-1
            indexData += vertexCount-4
        }
        return this
    }

    fun toMesh(): Mesh {
        if((vertexData.size / (Mesh.Stride/4)) % 4 != 0) {
            error("Must represent quads, missing at least ${4-(vertexData.size / Mesh.Stride % 4)} vertices")
        }
        return Mesh(vertexData.toFloatArray(), indexData.toIntArray())
    }
}