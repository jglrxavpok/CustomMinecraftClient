package org.jglrxavpok.mcclient.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.io.ByteArrayInputStream
import java.util.zip.DeflaterInputStream
import java.util.zip.InflaterInputStream

/**
 * Decodes a byte stream to a Packet
 */
class PacketDecoder(val networkSettings: NetworkSettings): ByteToMessageDecoder() {

    override fun decode(ctx: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        val startPos = buffer.readerIndex()
        val length = buffer.readVarInt()
        if(buffer.readableBytes() < length) {
            buffer.readerIndex(startPos)
            return
        }
        if(networkSettings.compressionThreshold > 0) {
            val dataLength = buffer.readVarInt()
            if(dataLength == 0) { // uncompressed
                out.add(decodePacket(buffer, ctx, length-1))
            } else { // compressed
                val compressed = ctx.alloc().buffer(dataLength)
                val inputBytes = ByteArray(length-1)
                buffer.readBytes(inputBytes)
                val bytes = InflaterInputStream(ByteArrayInputStream(inputBytes)).readBytes()
                compressed.writeBytes(bytes)
                out.add(decodePacket(compressed, ctx, dataLength))
                compressed.release()
            }
        } else {
            out.add(decodePacket(buffer, ctx, length))
        }
    }

    private fun decodePacket(buffer: ByteBuf, ctx: ChannelHandlerContext, length: Int): Packet {
        val packetID = buffer.readVarInt()
        val contents = ctx.alloc().buffer(length-1)
        buffer.readBytes(contents)
        return try {
            PacketRegistries.getRegistry(networkSettings.state, NetworkDirection.ClientBound).new(packetID).readFrom(contents)
        } catch (t: Throwable) {
            UnknownPacket(packetID, t)
        } finally {
            contents.release()
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
