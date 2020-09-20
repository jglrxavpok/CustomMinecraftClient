package org.jglrxavpok.mcclient.network.play.serverbound

import org.jglrxavpok.mcclient.network.Packet
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.data.DataType

class PlayerPositionAndRotationPacket: Packet {
    @Serializable(0, DataType.Double) var x: Double = 0.0
    @Serializable(1, DataType.Double) var feetY: Double = 0.0
    @Serializable(2, DataType.Double) var z: Double = 0.0
    @Serializable(3, DataType.Float) var yaw: Float = 0f
    @Serializable(4, DataType.Float) var pitch: Float = 0f
    @Serializable(5, DataType.Boolean) var onGround: Boolean = false
}