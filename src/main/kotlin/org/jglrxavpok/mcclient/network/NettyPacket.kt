package org.jglrxavpok.mcclient.network

import io.netty.buffer.ByteBuf

data class NettyPacket(val packetID: Int, val data: ByteBuf)