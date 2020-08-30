package org.jglrxavpok.mcclient.network.status

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Packet
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class StatusRequestPacket: Packet

class StatusResponsePacket: ServerPacket {
    @Serializable(0, DataType.String) var answer = ""

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("answer: $answer")
        // TODO
    }
}