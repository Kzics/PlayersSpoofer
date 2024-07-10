package com.spoofer.obj;

import net.minecraft.Util;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.lang.reflect.Field;

public class EmptyPacketListenerR4 extends ServerGamePacketListenerImpl {
    private static final Field KEEP_ALIVE_PENDING;

    private static final Field KEEP_ALIVE_CHALLENGE;

    private static final Field KEEP_ALIVE_TIME;

    static {
        try {
            KEEP_ALIVE_PENDING = ServerCommonPacketListenerImpl.class.getDeclaredField("keepAlivePending");
            KEEP_ALIVE_CHALLENGE = ServerCommonPacketListenerImpl.class.getDeclaredField("keepAliveChallenge");
            KEEP_ALIVE_TIME = ServerCommonPacketListenerImpl.class.getDeclaredField("keepAliveTime");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        KEEP_ALIVE_TIME.setAccessible(true);
        KEEP_ALIVE_CHALLENGE.setAccessible(true);
        KEEP_ALIVE_PENDING.setAccessible(true);
    }

    public EmptyPacketListenerR4(MinecraftServer server, Connection connection, ServerPlayer player,
                                 CommonListenerCookie commonListenerCookie) {
        super(server, connection, player,commonListenerCookie);

        try {
            KEEP_ALIVE_PENDING.set(this, Boolean.valueOf(false));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void keepConnectionAlive() {
        this.server.getProfiler().push("keepAlive");
        long currentTime = Util.getMillis();
        try {
            KEEP_ALIVE_PENDING.set(this, Boolean.FALSE);
            KEEP_ALIVE_CHALLENGE.set(this, currentTime);
            KEEP_ALIVE_TIME.set(this, currentTime);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        this.server.getProfiler().pop();
    }

   /* @Override
    public void handleMovePlayer(ServerboundMovePlayerPacket packetplayinflying) {
    }


    @Override
    public void handleInteract(ServerboundInteractPacket packet) {
        // Handle player interaction
    }

    @Override
    public void handleCustomPayload(ServerboundCustomPayloadPacket packet) {
        // Handle custom payloads
    }*/
}