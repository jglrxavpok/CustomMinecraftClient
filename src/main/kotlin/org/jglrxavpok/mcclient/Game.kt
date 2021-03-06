package org.jglrxavpok.mcclient

import org.jglrxavpok.mcclient.game.world.World
import org.jglrxavpok.mcclient.network.handshake.NetworkState
import org.jglrxavpok.mcclient.network.play.serverbound.PlayerMovement
import org.jglrxavpok.mcclient.network.play.serverbound.PlayerPositionAndRotationPacket
import org.jglrxavpok.mcclient.rendering.GameRenderer
import org.jglrxavpok.mcclient.rendering.WorldRenderer

object Game {
    var running: Boolean = true
        private set
    val world = World()

    private var time = 0.0

    init {

    }

    private fun getPlayerPosition() = WorldRenderer.camera.position // TODO: something less intricated with rendering

    fun update(deltaTime: Double) {
        // TODO: ticks
        time += deltaTime
        if(time > 0.5f) {
            time = 0.0
            val pos = getPlayerPosition()

            if(Client.networkSettings.state == NetworkState.Play) {
                Client.sendPacket(PlayerPositionAndRotationPacket().apply {
                    x = pos.x.toDouble()
                    feetY = pos.y.toDouble()
                    z = pos.z.toDouble()
                    onGround = false
                })
                Client.sendPacket(PlayerMovement().apply {
                    onGround = false
                })
            }
        }
    }

    fun stop() {
        running = false
    }
}
