package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.game.NetworkTag
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class Tags: ServerPacket {
    @Serializable(0, type = DataType.Array, subType = DataType.Tag) lateinit var blockTags: Array<NetworkTag>
    @Serializable(1, type = DataType.Array, subType = DataType.Tag) lateinit var itemTags: Array<NetworkTag>
    @Serializable(2, type = DataType.Array, subType = DataType.Tag) lateinit var fluidTags: Array<NetworkTag>
    @Serializable(3, type = DataType.Array, subType = DataType.Tag) lateinit var entityTags: Array<NetworkTag>

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("BlockTags ${blockTags.joinToString()}")
        println("itemTags ${itemTags.joinToString()}")
        println("fluidTags ${fluidTags.joinToString()}")
        println("entityTags ${entityTags.joinToString()}")
        // TODO
    }
}