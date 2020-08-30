package org.jglrxavpok.mcclient.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.data.DataType
import org.jglrxavpok.mcclient.network.handshake.HandshakePacket
import org.jglrxavpok.mcclient.network.handshake.NetworkState
import org.jglrxavpok.mcclient.network.login.LoginStart
import org.jglrxavpok.mcclient.network.login.LoginSuccess
import org.jglrxavpok.mcclient.network.login.SetCompression
import org.jglrxavpok.mcclient.network.play.JoinGame
import org.jglrxavpok.mcclient.network.status.StatusRequestPacket
import org.jglrxavpok.mcclient.network.status.StatusResponsePacket
import java.lang.RuntimeException
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

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
            .register(JoinGame::class, 0x24)
    val serverBoundPlay = PacketRegistry("ServerBound Play")

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

interface Packet {
    companion object {
        private val cache = mutableMapOf<KClass<out Packet>, MutableList<Pair<KProperty1<out Packet, *>, Serializable>>>()

        private fun precomputeProps(klass: KClass<out Packet>): MutableList<Pair<KProperty1<out Packet, *>, Serializable>> {
            return cache.computeIfAbsent(klass) {
                val result = mutableListOf<Pair<KProperty1<out Packet, *>, Serializable>>()
                klass.memberProperties
                        .mapNotNull {
                            val annot = it.findAnnotation<Serializable>() ?: return@mapNotNull null
                            it to annot
                        }
                        .sortedBy { it.second.ordinal }
                        .forEach { result += it }
                result
            }
        }
    }

    /**
     * Write the content of this packet to the given byte buffer
     */
    fun writeTo(buffer: ByteBuf) {
        val props = precomputeProps(this.javaClass.kotlin)
        for(prop in props) {
            val serializable = prop.second
            var value = prop.first.call(this)
            if(serializable.type == DataType.Array) {
                val array = value as Array<*>
                val size = array.size
                buffer.writeVarInt(size)
                for(elem in array) {
                    serializable.subType.write(buffer, elem)
                }
            } else if(serializable.isEnum) {
                value = (value as Enum<*>).ordinal
            }
            serializable.type.write(buffer, value)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    /**
     * Read the packet content from the given byte buffer
     */
    fun readFrom(buffer: ByteBuf): Packet {
        val props = precomputeProps(this.javaClass.kotlin)
        for(prop in props) {
            val serializable = prop.second
            val property = prop.first
            if (property is KMutableProperty<*>) {
                try {
                    var value = serializable.type.read(buffer)
                    if(serializable.type == DataType.Array) {
                        val length = DataType.VarInt.read(buffer) as Int
                        val array = java.lang.reflect.Array.newInstance(property.returnType.arguments[0].type!!.javaType as Class<*>?, length) as Array<Any?>
                        for(i in 0 until length) {
                            array[i] = serializable.subType.read(buffer)
                        }
                        value = array
                    } else if(serializable.isEnum) {
                        val type = property.javaField!!.type
                        val ordinal = value!!.toString().toInt() // toString().toInt() to handle cases of short, byte, and int at the same time
                        val constants = type.enumConstants
                        if(ordinal !in constants.indices) {
                            value = constants[0]
                            // TODO: replace with actual logger
                            System.err.println("Warning: invalid ordinal into enum constants of $type: $ordinal. Defaulting to value at ordinal 0: $value")
                        } else {
                            value = constants[ordinal]
                        }
                    }
                    (property as KMutableProperty<*>).setter.call(this, value)
                } catch (t: Throwable) {
                    throw RuntimeException("Failed to decode $property", t)
                }
            }
        }
        return this
    }
}

interface ServerPacket: Packet {

    /**
     * What does this packet do?
     */
    fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext)
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

class UnknownPacket(val id: Int, val cause: Throwable): ServerPacket {
    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        cause.printStackTrace()
        error("Unknown packet: $id (0x${id.toString(16)})")
    }
}