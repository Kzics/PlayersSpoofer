package com.spoofer.obj;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;

public class EmptyPacketEncoder extends PacketEncoder<ServerCommonPacketListener> {

    public static final ProtocolInfo PROTOCOL_INFO = (new ProtocolInfoBuilder(ConnectionProtocol.PLAY, PacketFlow.SERVERBOUND)).build(bf -> bf);
    public EmptyPacketEncoder() {
        super(PROTOCOL_INFO);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
    }
}
