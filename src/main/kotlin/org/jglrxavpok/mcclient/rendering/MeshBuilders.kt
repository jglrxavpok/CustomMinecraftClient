package org.jglrxavpok.mcclient.rendering

class MeshBuilders {

    private val builders = arrayOfNulls<MeshBuilder>(RenderPass.values().size)

    fun getBuilder(pass: RenderPass): MeshBuilder {
        val builder = builders[pass.ordinal]
        if(builder == null) {
            // TODO: different vertex formats
            builders[pass.ordinal] = MeshBuilder()
        }
        return builders[pass.ordinal]!!
    }

    fun toMeshes(): PassAwareMeshes {
        val meshes = arrayOfNulls<Mesh>(RenderPass.values().size)
        for(pass in RenderPass.values()) {
            val mesh = builders[pass.ordinal]?.toMesh()
            meshes[pass.ordinal] = mesh
        }
        return PassAwareMeshes(meshes)
    }

}