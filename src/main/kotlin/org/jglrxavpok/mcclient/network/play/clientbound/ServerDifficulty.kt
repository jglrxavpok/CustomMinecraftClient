package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.game.Difficulty
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class ServerDifficulty: ServerPacket {

    @Serializable(0, DataType.UnsignedByte, isEnum = true) lateinit var difficulty: Difficulty
    @Serializable(1, DataType.Boolean) var locked = false

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("server difficulty: $difficulty")
    }
}