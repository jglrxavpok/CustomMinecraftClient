package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.Gamemode
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class JoinGame: ServerPacket {

    @Serializable(0, DataType.Int) var entityId = -1
    @Serializable(1, DataType.Boolean) var hardcoreMode = false
    @Serializable(2, DataType.UnsignedByte, isEnum = true) lateinit var gamemode: Gamemode
    @Serializable(3, DataType.UnsignedByte, isEnum = true) lateinit var previousGamemode: Gamemode
    @Serializable(4, DataType.Array, subType = DataType.Identifier) lateinit var worldNames: Array<Identifier>
    @Serializable(5, DataType.NBTTag) lateinit var dimensionCodec: NBTCompound
    @Serializable(6, DataType.NBTTag) lateinit var dimension: NBTCompound
    @Serializable(7, DataType.Identifier) lateinit var worldName: Identifier
    @Serializable(8, DataType.Long) var hashedSeed: Long = 0L
    @Serializable(9, DataType.VarInt) var maxPlayers: Int = 0
    @Serializable(10, DataType.VarInt) var viewDistance: Int = 0
    @Serializable(11, DataType.Boolean) var reducedDebugInfo: Boolean = false
    @Serializable(12, DataType.Boolean) var enableRespawnScreen: Boolean = false
    @Serializable(13, DataType.Boolean) var isDebugWorld: Boolean = false
    @Serializable(14, DataType.Boolean) var isFlatWorld: Boolean = false

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("EntityID: $entityId")
        println("Hardcore: $hardcoreMode")
        println("Gamemode: $gamemode")
        println("Previous gamemode: $previousGamemode")
        println("World names: ${worldNames.joinToString()}")
        println("Dimension codec: $dimensionCodec")
        println("Dimension codec: $dimension")
        println("World name: $worldName")
        println("Hashed seed: $hashedSeed")
        println("Max Players: $maxPlayers")
        println("View distance: $viewDistance")
        println("reducedDebugInfo: $reducedDebugInfo")
        println("enableRespawnScreen: $enableRespawnScreen")
        println("isDebugWorld: $isDebugWorld")
        println("isFlatWorld: $isFlatWorld")
    }
}