package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class DeclareCommands: ServerPacket {

    @Serializable(0, type = DataType.ByteArray) lateinit var rawData: ByteArray // TODO: auto-handle compound types

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("declare commands")
        // TODO
    }
}