package org.jglrxavpok.mcclient.network.login

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class SetCompression: ServerPacket {
    @Serializable(0, DataType.VarInt) var threshold = 0

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        networkSettings.compressionThreshold = threshold
    }
}