package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class Disconnect: ServerPacket {

    @Serializable(0, DataType.String) lateinit var message: String

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        System.err.println("Disconnected from server, for reason: $message")
    }
}