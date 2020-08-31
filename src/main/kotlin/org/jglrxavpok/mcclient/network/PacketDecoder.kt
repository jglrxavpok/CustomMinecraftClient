package org.jglrxavpok.mcclient.network

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.io.ByteArrayInputStream
import java.util.zip.DeflaterInputStream
import java.util.zip.InflaterInputStream

/**
 * Decodes a byte stream to a Packet
 */
class PacketDecoder(val networkSettings: NetworkSettings): ByteToMessageDecoder() {

    // TODO: debug compression

    override fun decode(ctx: ChannelHandlerContext, buffer: ByteBuf, out: MutableList<Any>) {
        if(buffer.readableBytes() < 5) {
            // ensure we can read a var int
            return
        }
        val startPos = buffer.readerIndex()
        val length = buffer.readVarInt()
        if(buffer.readableBytes() < length) {
            // reset to start
            buffer.readerIndex(startPos)
            return
        }
        if(networkSettings.compressionThreshold > 0) {
            val dataLength = buffer.readVarInt()
            if(dataLength == 0) { // uncompressed
                out.add(decodePacket(buffer.readRetainedSlice(length-1), ctx, length-1))
            } else { // compressed
                if(dataLength < networkSettings.compressionThreshold) {
                    error("Invalid length for compressed packet: $dataLength, must be at least ${networkSettings.compressionThreshold}")
                }
                val compressed = ctx.alloc().buffer(dataLength)
                try {
                    val bytes = InflaterInputStream(ByteBufInputStream(buffer.readRetainedSlice(length - 1))).use { it.readBytes() }
                    compressed.writeBytes(bytes)
                    out.add(decodePacket(compressed, ctx, dataLength))
                } finally {
                    compressed.release()
                }
            }
        } else {
            out.add(decodePacket(buffer.readRetainedSlice(length), ctx, length))
        }
    }

    private fun decodePacket(buffer: ByteBuf, ctx: ChannelHandlerContext, length: Int): Packet {
        val packetID = buffer.readVarInt()
        val contents = buffer.readRetainedSlice(length-1)
        return try {
            val packet = PacketRegistries.getRegistry(networkSettings.state, NetworkDirection.ClientBound).new(packetID).readFrom(contents)
            if(contents.readerIndex() != contents.writerIndex()) {
                error("Packet $packet did not read entirety of buffer " +
                        "(readerIndex: ${contents.readerIndex()}, writerIndex: ${contents.writerIndex()}; remaining: ${contents.writerIndex()-contents.readerIndex()})")
            }
            return packet
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
