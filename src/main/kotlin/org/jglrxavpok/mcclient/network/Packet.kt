package org.jglrxavpok.mcclient.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.network.data.DataType
import java.lang.RuntimeException
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

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

class UnknownPacket(val id: Int, val cause: Throwable): ServerPacket {
    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        cause.printStackTrace()
        error("Unknown packet: $id (0x${id.toString(16)})")
    }
}