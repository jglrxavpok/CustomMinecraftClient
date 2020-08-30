package org.jglrxavpok.mcclient

import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import org.jglrxavpok.mcclient.network.*
import org.jglrxavpok.mcclient.network.handshake.HandshakePacket
import org.jglrxavpok.mcclient.network.handshake.NetworkState
import org.jglrxavpok.mcclient.network.login.LoginStart
import org.jglrxavpok.mcclient.network.status.StatusRequestPacket
import kotlin.concurrent.thread

/**
 * Entry point of the custom client
 */
object Client: ChannelInitializer<SocketChannel>() {

    val networkSettings = NetworkSettings(NetworkState.Handshake)

    @JvmStatic
    fun main(args: Array<String>) {
        setupNetty("127.0.0.1", 25565) {
            //requestStatus(it)
            login(it, "jglrxavpok")
        }
    }

    /**
     * Performs the handshake to the server
     */
    private fun handshake(channel: Channel, nextState: NetworkState): ChannelFuture {
        return channel.writeAndFlush(HandshakePacket().apply {
            protocolVersion = 751
            this.nextState = nextState
        })!!
    }

    /**
     * Login onto the server with the given username
     */
    private fun login(channel: Channel, username: String) {
        handshake(channel, NetworkState.Login).addListener {
            if(it.isSuccess) {
                networkSettings.state = NetworkState.Login
                channel.writeAndFlush(LoginStart().apply { this.username = username })
            }
        }
    }

    /**
     * Request the status of the server
     */
    private fun requestStatus(channel: Channel) {
        handshake(channel, NetworkState.Status).addListener {
            if(it.isSuccess) {
                networkSettings.state = NetworkState.Status
                channel.writeAndFlush(StatusRequestPacket())
            }
        }
    }

    fun setupNetty(serverAddress: String, port: Int, block: (Channel) -> Unit) {
        thread(name = "Netty Thread") {
            val workGroup = NioEventLoopGroup()
            try {
                val bootstrap = Bootstrap()
                    .group(workGroup)
                    .channel(NioSocketChannel::class.java)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(this)
                val future = bootstrap.connect(serverAddress, port).sync()

                println("Connected!")

                block(future.channel())

                future.channel().closeFuture().sync()
            } finally {
                workGroup.shutdownGracefully()
            }
        }
    }

    override fun initChannel(ch: SocketChannel?) {
        ch?.let {
            ch.pipeline().addLast(PacketDecoder(networkSettings), PacketDispatcher(networkSettings), // decoding pipeline
                                    PacketWriter(networkSettings), PacketEncoder(networkSettings)) // encoding pipeline

            ch.pipeline().addLast(ExceptionHandler())
            ch.pipeline().addFirst(ExceptionHandler())
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }
}
