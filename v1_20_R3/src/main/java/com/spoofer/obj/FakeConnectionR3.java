package com.spoofer.obj;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

public class FakeConnectionR3 extends Connection {
    public FakeConnectionR3(PacketFlow enumprotocoldirection) {
        super(enumprotocoldirection);
        FakeEventLoopGroup eventLoopGroup = new FakeEventLoopGroup();

        channel = new FakeChannel(null);
        eventLoopGroup.register(channel);

        address = new InetSocketAddress("127.0.0.1", 25565);
    }

    @Override
    public void setListener(PacketListener packetlistener) {
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

}