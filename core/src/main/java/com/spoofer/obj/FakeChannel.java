package com.spoofer.obj;


import java.net.SocketAddress;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import java.net.InetSocketAddress;

public class FakeChannel extends AbstractChannel {
    private final ChannelConfig config;
    private final ChannelMetadata metadata;
    private FakeChannel.State state;

    public FakeChannel() {
        super((Channel)null);
        this.state = FakeChannel.State.OPEN;
        this.metadata = new ChannelMetadata(false);
        this.config = new DefaultChannelConfig(this);
    }

    protected AbstractUnsafe newUnsafe() {
        return new AbstractUnsafe() {
            public void connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
                this.safeSetSuccess(channelPromise);
            }
        };
    }

    protected boolean isCompatible(EventLoop eventLoop) {
        return true;
    }

    protected SocketAddress localAddress0() {
        return new InetSocketAddress(25565);
    }

    protected SocketAddress remoteAddress0() {
        return new InetSocketAddress(25565);
    }

    protected void doBind(SocketAddress socketAddress) {
    }

    protected void doDisconnect() {
    }

    protected void doClose() {
        this.state = FakeChannel.State.CLOSED;
    }

    protected void doBeginRead() {
    }

    protected void doWrite(ChannelOutboundBuffer channelOutboundBuffer) {
        channelOutboundBuffer.remove();
    }

    public ChannelConfig config() {
        return this.config;
    }

    public boolean isOpen() {
        return this.state == FakeChannel.State.OPEN;
    }

    public boolean isActive() {
        return this.state == FakeChannel.State.OPEN;
    }

    public ChannelMetadata metadata() {
        return this.metadata;
    }

    public ChannelFuture write(Object msg) {
        return this.newSucceededFuture();
    }

    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.newSucceededFuture();
    }

    public ChannelFuture writeAndFlush(Object msg) {
        return this.newSucceededFuture();
    }

    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.newSucceededFuture();
    }

    private static enum State {
        OPEN,
        CLOSED;

        // $FF: synthetic method
        private static FakeChannel.State[] $values() {
            return new FakeChannel.State[]{OPEN, CLOSED};
        }
    }
}