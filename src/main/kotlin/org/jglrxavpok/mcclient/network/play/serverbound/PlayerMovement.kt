package org.jglrxavpok.mcclient.network.play.serverbound

import org.jglrxavpok.mcclient.network.Packet
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.data.DataType

class PlayerMovement: Packet {
    @Serializable(0, DataType.Boolean) var onGround: Boolean = false
}