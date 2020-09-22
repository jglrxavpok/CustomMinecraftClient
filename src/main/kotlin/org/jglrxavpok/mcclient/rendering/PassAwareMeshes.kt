package org.jglrxavpok.mcclient.rendering

/**
 * meshes is an array mapping a RenderPassType to a Mesh. Must be of length `RenderPassType.values().size`
 * 'null' denotes no mesh
 */
class PassAwareMeshes(val meshes: Array<Mesh?>) {

    fun getMesh(pass: RenderPass): Mesh? {
        return meshes[pass.ordinal]
    }

}
