package org.jglrxavpok.mcclient.network.login

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType
import org.jglrxavpok.mcclient.network.handshake.NetworkState
import java.util.*

class LoginSuccess: ServerPacket {

    @Serializable(0, DataType.UUID) var uuid = UUID.randomUUID()
    @Serializable(1, DataType.String) var username = ""

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("Logged in as $username with UUID $uuid")
        networkSettings.state = NetworkState.Play
    }
}