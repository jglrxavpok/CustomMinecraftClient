package org.jglrxavpok.mcclient.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class PacketDispatcher(val networkSettings: NetworkSettings): ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, incoming: Any) {
        val packet = incoming as? ServerPacket ?: error("Received invalid server packet: $incoming")
        packet.handle(networkSettings, ctx)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
