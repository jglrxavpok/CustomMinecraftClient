package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class HeldItemChange: ServerPacket {

    @Serializable(0, DataType.Byte) var selectedSlot: Byte = 0

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("Selected slot: $selectedSlot")
    }
}