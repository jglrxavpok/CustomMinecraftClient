package org.jglrxavpok.mcclient.network

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder

/**
 * Transforms a game packet to a packet useable by the `PacketWriter`
 */
class PacketEncoder(val networkSettings: NetworkSettings): MessageToMessageEncoder<Packet>() {

    companion object {
        val MaxPacketSize = 1024*1024*10
    }

    override fun encode(ctx: ChannelHandlerContext, msg: Packet, out: MutableList<Any>) {
        val buffer = ctx.alloc().buffer(MaxPacketSize)
        msg.writeTo(buffer)
        val packetID = PacketRegistries.getRegistry(networkSettings.state, NetworkDirection.ServerBound).getID(msg)
        out.add(NettyPacket(packetID, buffer))
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
