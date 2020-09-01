package org.jglrxavpok.mcclient.rendering

class MeshBuilder {

    private val vertexData = mutableListOf<Float>()
    private val indexData = mutableListOf<Int>()
    private var vertexCount: Int = 0

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