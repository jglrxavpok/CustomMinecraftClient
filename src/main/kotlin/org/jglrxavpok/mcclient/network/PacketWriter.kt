package org.jglrxavpok.mcclient.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import org.jglrxavpok.mcclient.network.data.VarInt
import java.io.ByteArrayOutputStream
import java.util.zip.DeflaterOutputStream

/**
 * Writes packet to the server, compressing them if needed
 */
class PacketWriter(val networkSettings: NetworkSettings): MessageToByteEncoder<NettyPacket>() {

    override fun encode(ctx: ChannelHandlerContext, msg: NettyPacket, out: ByteBuf) {
        val uncompressedLength = msg.data.writerIndex()+VarInt(msg.packetID).computeSize()/* packet id */
        if(networkSettings.compressionThreshold > 0) {
            // compress
            if(uncompressedLength >= networkSettings.compressionThreshold) {
                val baos = ByteArrayOutputStream()
                DeflaterOutputStream(baos).use { deflater ->
                    VarInt(msg.packetID).write(deflater)
                    msg.data.forEachByte { deflater.write(it.toInt()); true }
                }
                val bytes = baos.toByteArray()
                out.writeVarInt(VarInt(bytes.size).computeSize()+bytes.size)
                out.writeVarInt(bytes.size)
                out.writeBytes(bytes)
            } else { // uncompressed
                out.writeVarInt(uncompressedLength)
                out.writeVarInt(0) // uncompressed
                out.writeVarInt(msg.packetID)
                out.writeBytes(msg.data)
            }
        } else {
            out.writeVarInt(uncompressedLength)
            out.writeVarInt(msg.packetID)
            out.writeBytes(msg.data)
        }
        msg.data.release()
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}