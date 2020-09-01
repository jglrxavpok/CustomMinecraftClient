package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.hephaistos.mca.Palette
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.mcclient.Game
import org.jglrxavpok.mcclient.game.world.ChunkSection
import org.jglrxavpok.mcclient.network.*
import org.jglrxavpok.mcclient.network.data.DataType

class ChunkData: ServerPacket {

    @Serializable(0, DataType.Int) var chunkX: Int = 0
    @Serializable(1, DataType.Int) var chunkZ: Int = 0
    @Serializable(2, DataType.Boolean) var fullChunk: Boolean = false
    @Serializable(3, DataType.VarInt) var primaryBitMask: Int = 0
    @Serializable(4, DataType.NBTTag) lateinit var heightmaps: NBTCompound
    var biomes: IntArray? = null
    var data: ByteArray = ByteArray(0)
    var blockEntities: Array<NBTCompound> = emptyArray()

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        // TODO: handle fullChunk
        val chunk = Game.world.getOrCreateChunk(chunkX, chunkZ)
        val sectionData = ctx.alloc().buffer(data.size)
        sectionData.writeBytes(data)
        for(i in 0..15) {
            if(primaryBitMask and (1 shl i) == 0) {
                continue
            }
            readSection(chunk.sections[i], sectionData)
        }
        sectionData.release()
    }

    private fun readSection(chunkSection: ChunkSection, sectionData: ByteBuf) {
        val blockCount = DataType.Short.read(sectionData) as Short
        val bitsPerBlock = DataType.UnsignedByte.read(sectionData) as Byte
        var palette = IntArray(0)
        if(bitsPerBlock < 9) { // indirect
            val paletteLength = DataType.VarInt.read(sectionData) as Int
            palette = IntArray(paletteLength) {
                DataType.VarInt.read(sectionData) as Int
            }
        }
        val dataArrayLength = DataType.VarInt.read(sectionData) as Int
        val dataArray = LongArray(dataArrayLength) {
            DataType.Long.read(sectionData) as Long
        }
        val blocks = unpack(dataArray, bitsPerBlock.toInt())
        for(y in 0..15) {
            for(z in 0..15) {
                for (x in 0..15) {
                    val index = y*16*16+z*16+x
                    val paletteID = blocks[index]
                    val blockID = if(palette.isEmpty()) paletteID else palette[paletteID]
                    chunkSection.setBlockID(x, y, z, blockID)
                }
            }
        }
    }

    override fun readFrom(buffer: ByteBuf): Packet {
        super.readFrom(buffer)
        if(fullChunk) {
            val biomeCount = DataType.VarInt.read(buffer) as Int
            biomes = IntArray(biomeCount)
            for(i in 0 until biomeCount) {
                biomes!![i] = DataType.VarInt.read(buffer) as Int
            }
        }
        val size = DataType.VarInt.read(buffer) as Int
        data = ByteArray(size)
        buffer.readBytes(data)
        val blockEntitiesCount = DataType.VarInt.read(buffer) as Int
        blockEntities = Array(blockEntitiesCount) {
            DataType.NBTTag.read(buffer) as NBTCompound
        }
        return this
    }
}