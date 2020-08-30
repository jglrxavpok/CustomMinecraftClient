package org.jglrxavpok.mcclient.network

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise

class ExceptionHandler: ChannelDuplexHandler() {

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        except(ctx, cause)
    }

    private fun except(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }

    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        ctx.write(msg, promise.addListener {
            if(!it.isSuccess) {
                except(ctx, it.cause())
            }
        })
    }
}
