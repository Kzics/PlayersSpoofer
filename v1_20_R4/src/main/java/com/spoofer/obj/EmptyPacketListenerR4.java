package com.spoofer.obj;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class EmptyPacketListenerR4 extends ServerGamePacketListenerImpl {
    public EmptyPacketListenerR4(MinecraftServer server, Connection connection, ServerPlayer player,
                                 CommonListenerCookie commonListenerCookie) {
        super(server, connection, player,commonListenerCookie);
    }

    @Override
    public void handleMovePlayer(ServerboundMovePlayerPacket packetplayinflying) {
    }


    @Override
    public void handleInteract(ServerboundInteractPacket packet) {
        // Handle player interaction
    }

    @Override
    public void handleCustomPayload(ServerboundCustomPayloadPacket packet) {
        // Handle custom payloads
    }
}