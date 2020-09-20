package org.jglrxavpok.mcclient.rendering

import net.minestom.server.instance.block.Block
import org.jglrxavpok.hephaistos.mca.blockInsideChunk
import org.jglrxavpok.hephaistos.mca.blockToChunk
import org.jglrxavpok.mcclient.Game
import org.jglrxavpok.mcclient.IO
import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.blocks.getDefaultState
import org.jglrxavpok.mcclient.game.blocks.getIdentifier
import org.jglrxavpok.mcclient.game.blocks.getModel
import org.jglrxavpok.mcclient.game.world.Chunk
import org.jglrxavpok.mcclient.game.world.ChunkSection
import org.jglrxavpok.mcclient.rendering.atlases.Atlas
import org.joml.Matrix4fStack
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.concurrent.thread

class ChunkRenderer(val camera: Camera) {

    lateinit var blockAtlas: Atlas
        private set
    lateinit var chunkShader: Shader
        private set
    private var meshes = HashMap<ChunkSection, Mesh>()
    // is a chunk section being built?
    // allow parallel preparation of chunk meshes
    private var building = mutableMapOf<ChunkSection, Boolean>()

    // TODO: transparent layers

    fun init() {
        chunkShader = Shader("default")
        chunkShader.use {
            it.updateUniform("albedo", 0)
        }

        val textureMap = mutableMapOf<Identifier, () -> BufferedImage>()

        fun registerTexture(id: Identifier, textureID: Identifier = id) {
            textureMap[id] = {
                try {
                    IO.openStream(textureID.prependPath("textures").withExtension("png")).use(ImageIO::read)
                } catch (e: Exception) {
                    e.printStackTrace()
                    MissingImage
                }
            }
        }

        for(block in Block.values()) {
            if(block == Block.AIR)
                continue
            val textures = block.getModel().backingModel.textures()
            for(t in textures) {
                if(!textureMap.containsKey(t)) {
                    registerTexture(t)
                }
            }
        }

        registerTexture(Identifier("water"), Identifier("block/water_still"))
        registerTexture(Identifier("lava"), Identifier("block/lava_still"))

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
            if(building[section] != true) { // false or null
                buildMesh(section)
            }
        }
        meshes[section]?.render()
    }

    private fun buildMesh(section: ChunkSection) {
        building[section] = true
        thread {
            val matrixStack = Matrix4fStack(256).apply { identity() }.pushMatrix()
            val meshBuilder = MeshBuilder()
            for(y in 0..15) {
                for(z in 0..15) {
                    for(x in 0..15) {
                        val block = Block.fromStateId(section.getBlockID(x, y, z))
                        if(shouldRender(block, section, x, y, z)) {
                            renderBlock(matrixStack, block, meshBuilder, x+section.x*16, y+section.y*16, z+section.z*16)
                        }
                    }
                }
            }

            GameRenderer.nextFrame {
                meshes[section] = meshBuilder.toMesh()
                building[section] = false
            }
        }
    }

    private fun getBlockID(x: Int, y: Int, z: Int): Short {
        val chunk = Game.world.getChunk(x.blockToChunk(), z.blockToChunk()) ?: return Block.BEDROCK.blockId
        val sectionIndex = y/16
        if(sectionIndex !in 0..15) return Block.BEDROCK.blockId
        val section = chunk.sections[sectionIndex]
        return section.getBlockID(x.blockInsideChunk(), y.blockInsideChunk(), z.blockInsideChunk())
    }

    private fun shouldRender(block: Block, section: ChunkSection, x: Int, y: Int, z: Int): Boolean {
        if(block.isLiquid)
            return true
        // TODO: section borders
        // TODO: cullfaces from models
        val worldX = x+section.x*16
        val worldZ = z+section.z*16
        val worldY = y+section.y*16
        val top = Block.fromStateId(getBlockID(worldX,worldY+1,worldZ))
        val bottom = Block.fromStateId(getBlockID(worldX,worldY-1,worldZ))
        val east = Block.fromStateId(getBlockID(worldX+1,worldY,worldZ))
        val west = Block.fromStateId(getBlockID(worldX-1,worldY+1,worldZ))
        val north = Block.fromStateId(getBlockID(worldX,worldY,worldZ-1))
        val south = Block.fromStateId(getBlockID(worldX,worldY,worldZ+1))
        return !top.isSolid || !bottom.isSolid
                || !east.isSolid || !west.isSolid
                || !north.isSolid || !south.isSolid
    }

    // TODO: block states
    // TODO: block faces
    // TODO: use actual models
    private fun renderBlock(matrixStack: Matrix4fStack, block: Block, meshBuilder: MeshBuilder, x: Int, y: Int, z: Int) {
        if(block == Block.AIR)
            return
        matrixStack.pushMatrix()
        matrixStack.translate(x.toFloat(), y.toFloat(), z.toFloat())
        when {
            block.isLiquid -> {
                // TODO: move to other special layer
                renderLiquid(matrixStack, block, meshBuilder, x, y, z)
            }

            else -> {
                val model = block.getModel()
                model.backingModel.fillQuads(matrixStack, meshBuilder, /*TODO: get block state*/block.getDefaultState(), x, y, z)
            }
        }
        matrixStack.popMatrix()
    }

    private fun renderLiquid(matrixStack: Matrix4fStack, block: Block, meshBuilder: MeshBuilder, x: Int, y: Int, z: Int) {
        val top = Block.fromStateId(getBlockID(x, y +1, z))
        val bottom = Block.fromStateId(getBlockID(x, y -1, z))
        val east = Block.fromStateId(getBlockID(x +1, y, z))
        val west = Block.fromStateId(getBlockID(x -1, y +1, z))
        val north = Block.fromStateId(getBlockID(x, y, z -1))
        val south = Block.fromStateId(getBlockID(x, y, z +1))

        val sprite = blockAtlas.getSprite(block.getIdentifier())

        val level = 1.0f // TODO: based on block state
        val minX = 0.0f
        val maxX = 1.0f
        val minY = 0.0f
        val maxY = 1.0f*level*15f/16f
        val minZ = 0.0f
        val maxZ = 1.0f
        val minU = sprite.minU
        val maxU = sprite.maxU
        val minV = sprite.minV
        val maxV = sprite.maxV
        if(top != block) {
            // TODO: don't hardcode water color
            val r = if(block == Block.WATER) 0x3F/256f else 1f
            val g = if(block == Block.WATER) 0x76/256f else 1f
            val b = if(block == Block.WATER) 0xE4/256f else 1f
            meshBuilder
                .vertex(matrixStack, minX, maxY, minZ, minU, minV, r, g, b)
                .vertex(matrixStack, maxX, maxY, minZ, maxU, minV, r, g, b)
                .vertex(matrixStack, maxX, maxY, maxZ, maxU, maxV, r, g, b)
                .vertex(matrixStack, minX, maxY, maxZ, minU, maxV, r, g, b)
        }

        // TODO: other faces
    }

    fun forceRerender() {
        meshes.clear()
        println("rerender requested")
    }
}
