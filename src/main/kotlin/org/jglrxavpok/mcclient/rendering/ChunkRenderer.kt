package org.jglrxavpok.mcclient.rendering

import net.minestom.server.instance.block.Block
import org.jglrxavpok.mcclient.IO
import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.blocks.getDefaultState
import org.jglrxavpok.mcclient.game.blocks.getModel
import org.jglrxavpok.mcclient.game.world.Chunk
import org.jglrxavpok.mcclient.game.world.ChunkSection
import org.jglrxavpok.mcclient.rendering.atlases.Atlas
import org.joml.Matrix4fStack
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ChunkRenderer(val camera: Camera) {

    lateinit var blockAtlas: Atlas
        private set
    lateinit var chunkShader: Shader
        private set
    private var meshes = HashMap<ChunkSection, Mesh>()
    private val matrixStack = Matrix4fStack(256).apply { identity() }.pushMatrix()

    fun init() {
        chunkShader = Shader("default")
        chunkShader.use {
            it.updateUniform("albedo", 0)
        }

        val textureMap = mutableMapOf<Identifier, () -> BufferedImage>()
        for(block in Block.values()) {
            if(block == Block.AIR)
                continue
            val textures = block.getModel().backingModel.textures()
            for(t in textures) {
                if(!textureMap.containsKey(t)) {
                    textureMap[t] = {
                        try {
                            IO.openStream(t.prependPath("textures").withExtension("png")).use(ImageIO::read)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            MissingImage
                        }
                    }
                }
            }
        }

        blockAtlas = Atlas(textureMap)
    }

    fun renderChunk(chunk: Chunk) {
        blockAtlas.bind()
        chunkShader.use {
            camera.updateShader(it)
            for(section in chunk.sections) {
                renderSection(section)
            }
        }
        // TODO: entities and such
    }

    private fun renderSection(section: ChunkSection) {
        if(!meshes.containsKey(section)) {
            buildMesh(section)
        }
        meshes[section]?.render()
    }

    private fun buildMesh(section: ChunkSection) {
        // TODO
        val meshBuilder = MeshBuilder()
        for(y in 0..15) {
            for(z in 0..15) {
                for(x in 0..15) {
                    val block = Block.fromStateId(section.getBlockID(x, y, z))
                    if(shouldRender(block, section, x, y, z)) {
                        renderBlock(block, meshBuilder, x+section.x*16, y+section.y*16, z+section.z*16)
                    }
                }
            }
        }
        meshes[section] = meshBuilder.toMesh()
    }

    private fun shouldRender(block: Block, section: ChunkSection, x: Int, y: Int, z: Int): Boolean {
        // TODO: section borders
        // TODO: cullfaces from models
        return when {
            x != 0 && x != 15 && y != 0 && y != 15 && z != 0 && z != 15 -> {
                val top = Block.fromStateId(section.getBlockID(x,y+1,z))
                val bottom = Block.fromStateId(section.getBlockID(x,y-1,z))
                val east = Block.fromStateId(section.getBlockID(x+1,y,z))
                val west = Block.fromStateId(section.getBlockID(x-1,y+1,z))
                val north = Block.fromStateId(section.getBlockID(x,y,z-1))
                val south = Block.fromStateId(section.getBlockID(x,y,z+1))
                !top.isSolid || !bottom.isSolid
                        || !east.isSolid || !west.isSolid
                        || !north.isSolid || !south.isSolid
            }

            else -> true
        }
    }

    // TODO: block states
    // TODO: block faces
    // TODO: use actual models
    private fun renderBlock(block: Block, meshBuilder: MeshBuilder, x: Int, y: Int, z: Int) {
        if(block == Block.AIR)
            return
        val model = block.getModel()
        matrixStack.pushMatrix()
        matrixStack.translate(x.toFloat(), y.toFloat(), z.toFloat())
        model.backingModel.fillQuads(matrixStack, meshBuilder/*TODO: get block state*/, block.getDefaultState(), x, y, z)
        matrixStack.popMatrix()
    }

    fun forceRerender() {
        meshes.clear()
        println("rerender requested")
    }
}
