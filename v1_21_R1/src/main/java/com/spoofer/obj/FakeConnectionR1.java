package com.spoofer.obj;

import io.netty.channel.ChannelHandler;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
import net.minecraft.network.BandwidthDebugMonitor;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.util.debugchart.LocalSampleLogger;
import org.jetbrains.annotations.Nullable;

public class FakeConnectionR1 extends Connection {
    private static final EventLoop LOOP = (EventLoop)new DefaultEventLoop();
    public FakeConnectionR1(PacketFlow enumprotocoldirection) {
        super(enumprotocoldirection);
        FakeChannel fakeChannel = new FakeChannel();
        LOOP.register(fakeChannel);

        configureSerialization(fakeChannel.pipeline(),PacketFlow.SERVERBOUND,false,new BandwidthDebugMonitor(new LocalSampleLogger(240)));
        fakeChannel.pipeline().addLast("encoder", (ChannelHandler)new EmptyPacketEncoder());
        fakeChannel.pipeline().addLast("packet_handler", (ChannelHandler)this);

        this.channel = fakeChannel;
        // this.channel = new FakeChannel(null);

        address = fakeChannel.remoteAddress();
        //address = new InetSocketAddress("127.0.0.1", 25565);
    }
    @Override
    public void flushChannel() {
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void send(Packet<?> packet) {
    }

    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener packetsendlistener) {

    }
    @Override
    public void send(Packet<?> packet, @Nullable PacketSendListener packetsendlistener, boolean flag) {
    }

    @Override
    public void setListenerForServerboundHandshake(PacketListener listener) {
        try {
            java.lang.reflect.Field packetListenerField = Connection.class.getDeclaredField("packetListener");
            packetListenerField.setAccessible(true);
            packetListenerField.set(this, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
