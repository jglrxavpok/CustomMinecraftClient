package org.jglrxavpok.mcclient.network.handshake

import org.jglrxavpok.mcclient.network.Packet
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.data.DataType
import org.jglrxavpok.mcclient.network.data.VarInt

class HandshakePacket(): Packet {

    @Serializable(0, DataType.VarInt) var protocolVersion = 0
    @Serializable(1,DataType.String) val serverAddress: String = ""
    @Serializable(2,DataType.UnsignedShort) var serverPort: Short = 25565
    @Serializable(3,DataType.VarInt, isEnum = true) var nextState: NetworkState = NetworkState.Status

}


enum class NetworkState {
    Play, Status, Login, Handshake
}