package org.jglrxavpok.mcclient.network.play.serverbound

import org.jglrxavpok.mcclient.network.Packet
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.data.DataType

class KeepAlive: Packet {
    @Serializable(0, DataType.Long) var id: Long = 0
}