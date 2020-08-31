package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class EntityStatus: ServerPacket {

    @Serializable(0, DataType.Int) var entityID: Int = 0
    @Serializable(1, DataType.Byte) var status: Int = 0

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("entity status $entityID, $status")
    }
}