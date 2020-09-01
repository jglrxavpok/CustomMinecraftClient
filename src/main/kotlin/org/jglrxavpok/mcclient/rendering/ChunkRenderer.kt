package org.jglrxavpok.mcclient.rendering

import org.jglrxavpok.mcclient.game.blocks.Blocks
import org.jglrxavpok.mcclient.game.world.Chunk
import org.jglrxavpok.mcclient.game.world.ChunkSection
import org.jglrxavpok.mcclient.rendering.atlases.AtlasSprite
import javax.imageio.ImageIO

class ChunkRenderer(val camera: Camera) {

    private lateinit var blockAtlas: Texture
    lateinit var chunkShader: Shader
    private var meshes = HashMap<ChunkSection, Mesh>()

    fun init() {
        chunkShader = Shader("default")
        chunkShader.use {
            it.updateUniform("albedo", 0)
        }
        blockAtlas = Texture(ImageIO.read(javaClass.getResourceAsStream("/textures/blocks.png")))
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
                    val block = Blocks.fromID(section.getBlockID(x, y, z))
                    if(shouldRender(block, section, x, y, z)) {
                        renderBlock(block, meshBuilder, x+section.x*16, y+section.y*16, z+section.z*16)
                    }
                }
            }
        }
        meshes[section] = meshBuilder.toMesh()
    }

    private fun shouldRender(block: Blocks, section: ChunkSection, x: Int, y: Int, z: Int): Boolean {
        // TODO: section borders
        return when {
            x != 0 && x != 15 && y != 0 && y != 15 && z != 0 && z != 15 -> {
                val top = Blocks.fromID(section.getBlockID(x,y+1,z))
                val bottom = Blocks.fromID(section.getBlockID(x,y-1,z))
                val east = Blocks.fromID(section.getBlockID(x+1,y,z))
                val west = Blocks.fromID(section.getBlockID(x-1,y+1,z))
                val north = Blocks.fromID(section.getBlockID(x,y,z-1))
                val south = Blocks.fromID(section.getBlockID(x,y,z+1))
                top == Blocks.Air || bottom == Blocks.Air
                        || east == Blocks.Air || west == Blocks.Air
                        || north == Blocks.Air || south == Blocks.Air
            }

            else -> true
        }
    }

    // TODO: block states
    // TODO: block faces
    // TODO: use actual models
    private fun renderBlock(block: Blocks, meshBuilder: MeshBuilder, x: Int, y: Int, z: Int) {
        if(block == Blocks.Air)
            return
        val sprite = getSprite(block)
        // back
        meshBuilder
                .vertex(x.toFloat(), y.toFloat(), z.toFloat(), sprite.minU, sprite.minV)
                .vertex(x.toFloat()+1f, y.toFloat(), z.toFloat(), sprite.maxU, sprite.minV)
                .vertex(x.toFloat()+1f, y.toFloat()+1f, z.toFloat(), sprite.maxU, sprite.maxV)
                .vertex(x.toFloat(), y.toFloat()+1f, z.toFloat(), sprite.minU, sprite.maxV)

        // front
        meshBuilder
                .vertex(x.toFloat(), y.toFloat(), z.toFloat()+1f, sprite.minU, sprite.minV)
                .vertex(x.toFloat()+1f, y.toFloat(), z.toFloat()+1f, sprite.maxU, sprite.minV)
                .vertex(x.toFloat()+1f, y.toFloat()+1f, z.toFloat()+1f, sprite.maxU, sprite.maxV)
                .vertex(x.toFloat(), y.toFloat()+1f, z.toFloat()+1f, sprite.minU, sprite.maxV)

        // top
        meshBuilder
                .vertex(x.toFloat(), y.toFloat()+1f, z.toFloat(), sprite.minU, sprite.minV)
                .vertex(x.toFloat()+1f, y.toFloat()+1f, z.toFloat(), sprite.maxU, sprite.minV)
                .vertex(x.toFloat()+1f, y.toFloat()+1f, z.toFloat()+1f, sprite.maxU, sprite.maxV)
                .vertex(x.toFloat(), y.toFloat()+1f, z.toFloat()+1f, sprite.minU, sprite.maxV)

        // bottom
        meshBuilder
                .vertex(x.toFloat(), y.toFloat(), z.toFloat(), sprite.minU, sprite.minV)
                .vertex(x.toFloat()+1f, y.toFloat(), z.toFloat(), sprite.maxU, sprite.minV)
                .vertex(x.toFloat()+1f, y.toFloat(), z.toFloat()+1f, sprite.maxU, sprite.maxV)
                .vertex(x.toFloat(), y.toFloat(), z.toFloat()+1f, sprite.minU, sprite.maxV)

        // east
        meshBuilder
                .vertex(x.toFloat()+1f, y.toFloat(), z.toFloat(), sprite.minU, sprite.minV)
                .vertex(x.toFloat()+1f, y.toFloat()+1f, z.toFloat(), sprite.maxU, sprite.minV)
                .vertex(x.toFloat()+1f, y.toFloat()+1f, z.toFloat()+1f, sprite.maxU, sprite.maxV)
                .vertex(x.toFloat()+1f, y.toFloat(), z.toFloat()+1f, sprite.minU, sprite.maxV)

        // west
        meshBuilder
                .vertex(x.toFloat(), y.toFloat(), z.toFloat(), sprite.minU, sprite.minV)
                .vertex(x.toFloat(), y.toFloat()+1f, z.toFloat(), sprite.maxU, sprite.minV)
                .vertex(x.toFloat(), y.toFloat()+1f, z.toFloat()+1f, sprite.maxU, sprite.maxV)
                .vertex(x.toFloat(), y.toFloat(), z.toFloat()+1f, sprite.minU, sprite.maxV)
    }

    private fun getSprite(block: Blocks): AtlasSprite {
        // TODO
        val stoneSprite by lazy { AtlasSprite(16.5f/48f, 0f,32f/48f, 1f) }
        val dirtSprite by lazy { AtlasSprite(0.5f, 0f, 16f/48f, 1f) }
        val grassSprite by lazy { AtlasSprite(32.5f/48f, 0f, 1f, 1f) }
        return when(block) {
            Blocks.Dirt -> dirtSprite
            Blocks.Grass -> grassSprite
            else -> stoneSprite
        }
    }

    fun forceRerender() {
        meshes.clear()
        println("rerender requested")
    }
}
