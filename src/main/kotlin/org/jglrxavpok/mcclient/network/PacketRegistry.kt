package org.jglrxavpok.mcclient.network

import org.jglrxavpok.mcclient.network.handshake.HandshakePacket
import org.jglrxavpok.mcclient.network.handshake.NetworkState
import org.jglrxavpok.mcclient.network.login.LoginStart
import org.jglrxavpok.mcclient.network.login.LoginSuccess
import org.jglrxavpok.mcclient.network.login.SetCompression
import org.jglrxavpok.mcclient.network.play.clientbound.*
import org.jglrxavpok.mcclient.network.play.serverbound.PlayerPositionAndRotationPacket
import org.jglrxavpok.mcclient.network.status.StatusRequestPacket
import org.jglrxavpok.mcclient.network.status.StatusResponsePacket
import kotlin.reflect.KClass

/**
 * Registries for packets, based on the current protocol type
 */
object PacketRegistries {
    val clientBoundHandshake = PacketRegistry("ClientBound Handshake")
    val serverBoundHandshake = PacketRegistry("ServerBound Handshake")
        .register(HandshakePacket::class, 0x00)

    val clientBoundStatus = PacketRegistry("ClientBound Status")
        .register(StatusResponsePacket::class, 0x00)
    val serverBoundStatus = PacketRegistry("ServerBound Status")
        .register(StatusRequestPacket::class, 0x00)

    val clientBoundLogin = PacketRegistry("ClientBound Login")
            .register(LoginSuccess::class, 0x02)
            .register(SetCompression::class, 0x03)
    val serverBoundLogin = PacketRegistry("ServerBound Login")
            .register(LoginStart::class, 0x00)

    val clientBoundPlay = PacketRegistry("ClientBound Play")
            .register(ServerDifficulty::class, 0x0D)
            .register(DeclareCommands::class, 0x10)
            .register(PluginMessage::class, 0x17)
            .register(EntityStatus::class, 0x1A)
            .register(UnloadChunk::class, 0x1C)
            .register(ChunkData::class, 0x20)
            .register(JoinGame::class, 0x24)
            .register(PlayerAbilitiesClientBound::class, 0x30)
            .register(SetPlayerPositionAndLook::class, 0x34)
            .register(UnlockRecipes::class, 0x35)
            .register(HeldItemChange::class, 0x3F)
            .register(DeclareRecipes::class, 0x5A)
            .register(Tags::class, 0x5B)
    val serverBoundPlay = PacketRegistry("ServerBound Play")
            .register(PlayerPositionAndRotationPacket::class, 0x13)

    fun getRegistry(state: NetworkState, direction: NetworkDirection): PacketRegistry {
        return when(state) {
            NetworkState.Handshake -> if(direction == NetworkDirection.ClientBound) clientBoundHandshake else serverBoundHandshake
            NetworkState.Status -> if(direction == NetworkDirection.ClientBound) clientBoundStatus else serverBoundStatus
            NetworkState.Login -> if(direction == NetworkDirection.ClientBound) clientBoundLogin else serverBoundLogin
            NetworkState.Play -> if(direction == NetworkDirection.ClientBound) clientBoundPlay else serverBoundPlay

            else -> TODO()
        }
    }
}

class PacketRegistry(val name: String) {
    private val class2id = mutableMapOf<KClass<out Packet>, Int>()
    private val id2class = mutableMapOf<Int, KClass<out Packet>>()

    fun register(klass: KClass<out Packet>, id: Int): PacketRegistry {
        class2id[klass] = id
        id2class[id] = klass
        return this
    }

    /**
     * Returns the protocol ID used for the given packet
     */
    fun getID(msg: Packet): Int {
        return class2id[msg::class] ?: error("Unregistered packet type: $msg in $name")
    }

    /**
     * Instantiate a new Packet from the given id
     *
     * Throws if the ID is not known
     */
    fun new(id: Int): Packet {
        val klass = id2class[id] ?: error("Unknown packet ID: $id (0x${id.toString(16)}) in $name")
        return klass.constructors.first().call()
    }
}