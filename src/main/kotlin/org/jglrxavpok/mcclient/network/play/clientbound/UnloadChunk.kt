package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class UnloadChunk: ServerPacket {

    @Serializable(0, DataType.Int) var chunkX = 0
    @Serializable(1, DataType.Int) var chunkZ = 0

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        // TODO("Not yet implemented")
        println(">> Unload $chunkX,$chunkZ")
        System.err.println("$this")
    }
}