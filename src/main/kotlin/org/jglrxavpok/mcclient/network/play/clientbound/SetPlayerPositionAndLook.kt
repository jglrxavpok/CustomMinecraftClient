package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.input.Input
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType
import org.jglrxavpok.mcclient.rendering.GameRenderer
import org.jglrxavpok.mcclient.rendering.WorldRenderer

class SetPlayerPositionAndLook: ServerPacket {

    @Serializable(0, DataType.Double) var x: Double = 0.0
    @Serializable(1, DataType.Double) var y: Double = 0.0
    @Serializable(2, DataType.Double) var z: Double = 0.0
    @Serializable(3, DataType.Float) var yaw: Float = 0f
    @Serializable(4, DataType.Float) var pitch: Float = 0f
    @Serializable(5, DataType.Byte) var flags: Byte = 0
    @Serializable(6, DataType.VarInt) var teleportID: Int = 0

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        // TODO: cleaner code
        WorldRenderer.camera.position.set(x, y, z)
        Input.yaw = yaw/180f*Math.PI.toFloat()
        Input.pitch = pitch/180f*Math.PI.toFloat()
        println("set position and look $x, $y, $z")
    }
}