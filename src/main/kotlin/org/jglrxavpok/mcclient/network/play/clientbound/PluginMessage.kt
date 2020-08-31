package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class PluginMessage: ServerPacket {

    @Serializable(0, DataType.Identifier) lateinit var identifier: Identifier
    @Serializable(1, DataType.ByteArray) lateinit var data: ByteArray

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("Plugin message: $identifier, $data")
    }
}