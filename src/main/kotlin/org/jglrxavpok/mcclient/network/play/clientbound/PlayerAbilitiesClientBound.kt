package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class PlayerAbilitiesClientBound: ServerPacket {

    @Serializable(0, DataType.Byte) var flags: Byte = 0
    @Serializable(1, DataType.Float) var flyingSpeed = 0f
    @Serializable(2, DataType.Float) var fovModifier = 0f

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("abilities: $flags, $flyingSpeed, $fovModifier")
    }
}