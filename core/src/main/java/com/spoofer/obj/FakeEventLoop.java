package com.spoofer.obj;

import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

class FakeEventLoop extends AbstractEventLoop {
    protected FakeEventLoop() {
        super(null);
    }

    @Override
    public EventLoopGroup parent() {
        return null;
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public Future<?> shutdownGracefully(long l, long l1, TimeUnit timeUnit) {
        return null;
    }

    @Override
    public Future<?> terminationFuture() {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return true;
    }

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public ChannelFuture register(Channel channel) {
        return register(new DefaultChannelPromise(channel, this));
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        ((AbstractChannel) promise.channel()).unsafe().register(this, promise);
        return promise;
    }

    @Override
    public ChannelFuture register(Channel channel, ChannelPromise channelPromise) {
        return null;
    }
}
