package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType
import org.jglrxavpok.mcclient.network.play.serverbound.KeepAlive as KeepAliveClient

class KeepAlive: ServerPacket {

    @Serializable(0, DataType.Long) var id: Long = 0

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(KeepAliveClient().apply { this.id = id })
    }
}