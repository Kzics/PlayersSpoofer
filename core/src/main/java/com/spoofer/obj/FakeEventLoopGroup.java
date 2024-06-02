package com.spoofer.obj;

import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class FakeEventLoopGroup extends AbstractEventLoopGroup {
    private final FakeEventLoop eventLoop = new FakeEventLoop();

    @Override
    public EventLoop next() {
        return eventLoop;
    }

    @Override
    public Iterator<EventExecutor> iterator() {
        return Collections.singleton((EventExecutor) eventLoop).iterator();
    }


    @Override
    public ChannelFuture register(Channel channel) {
        return eventLoop.register(channel);
    }

    @Override
    public ChannelFuture register(ChannelPromise promise) {
        return eventLoop.register(promise);
    }

    @Override
    public ChannelFuture register(Channel channel, ChannelPromise promise) {
        return eventLoop.register(channel, promise);
    }

    @Override
    public boolean isShuttingDown() {
        return false;
    }

    @Override
    public Future<?> shutdownGracefully(long l, long l1, TimeUnit timeUnit) {
        return eventLoop.newSucceededFuture(null);
    }

    @Override
    public Future<?> terminationFuture() {
        return eventLoop.newSucceededFuture(null);
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
}
