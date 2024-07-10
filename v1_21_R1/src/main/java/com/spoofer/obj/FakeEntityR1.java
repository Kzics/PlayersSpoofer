package com.spoofer.obj;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.spoofer.PlayersSpoof;
import com.spoofer.manager.SkinDownloader;
import net.minecraft.network.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class FakeEntityR1 implements IFakeEntity {

    protected String name;
    protected boolean visible;
    protected String rank;
    protected String suffix;
    protected Location location;
    private ServerPlayer npc;
    private UUID uuid;

    public FakeEntityR1(String name, Location location) {
        this(name, true, "default", "", location);
    }

    public FakeEntityR1(String name, boolean visible, Location location) {
        this(name, visible, "default", "", location);
    }

    public FakeEntityR1(String name, boolean visible, String rank, String suffix, Location location) {
        this.name = name;
        this.visible = visible;
        this.rank = rank;
        this.location = location;
        this.suffix = suffix;
    }

    public void setSkin(GameProfile profile, Player player){
        try {
            Property textures = SkinDownloader.fetchRandomTexture();
            profile.getProperties().put("textures", textures);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            EntityDataAccessor<Byte> skinLayerMask = new EntityDataAccessor<>(17, EntityDataSerializers.BYTE);
            this.npc.getEntityData().set(skinLayerMask, (byte) 127);
            ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData().getNonDefaultValues());
            sendPacket(player, packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void sendPacket(Player player, Packet<?>... packets) {
        for (Packet packet : packets){
            ((CraftPlayer)player).getHandle().connection.send(packet);

        }
    }

    public UUID getUuid() {
        return uuid;
    }


    public void create() {
        Server bukkitServer = Bukkit.getServer();
        MinecraftServer server;
        try {
            Class<?> craftServerClass = Class.forName("org.bukkit.craftbukkit.v1_21_R1.CraftServer");
            Method getServerMethod = craftServerClass.getMethod("getServer");
            server = (MinecraftServer) getServerMethod.invoke(bukkitServer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ServerLevel worldServer;
        try {
            Class<?> worldServerClass = Class.forName("org.bukkit.craftbukkit.v1_21_R1.CraftWorld");
            Method getHandleMethod = worldServerClass.getMethod("getHandle");
            worldServer = (ServerLevel) getHandleMethod.invoke(location.getWorld());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.uuid = UUID.randomUUID();
        GameProfile gameProfile = new GameProfile(uuid, name);
        ClientInformation info = new ClientInformation("en_us", 0, ChatVisiblity.FULL, false, 0, HumanoidArm.RIGHT, true, true);
        this.npc = new ServerPlayer(server, worldServer, gameProfile, info);
        for (Player player : Bukkit.getOnlinePlayers()) setSkin(npc.getGameProfile(), player);

        FakeConnectionR1 connection = new FakeConnectionR1(PacketFlow.SERVERBOUND);
        this.setupNetworkManager(connection);

        CommonListenerCookie cookie = new CommonListenerCookie(gameProfile, new Random().nextInt(150),ClientInformation.createDefault(),true);
        npc.connection = new EmptyPacketListenerR1(server, connection, npc, cookie);

        this.npc.listName = Component.empty().append(ChatColor.translateAlternateColorCodes('&', rank + " " + suffix + name));
        this.npc.displayName = Component.empty().append(ChatColor.translateAlternateColorCodes('&', rank + " " + suffix + name)).getString();
        PlayerList playerList = server.getPlayerList();

        playerList.placeNewPlayer(connection, npc, cookie);
    }

    private void setupNetworkManager(FakeConnectionR1 networkManager) {
        try {
            ProtocolInfo<?> protocolInfo = EmptyPacketEncoder.PROTOCOL_INFO;
            UnconfiguredPipelineHandler.OutboundConfigurationTask unconfiguredpipelinehandler_d = UnconfiguredPipelineHandler.setupOutboundProtocol(protocolInfo);
            BundlerInfo EMPTY = new BundlerInfo() {
                public void unbundlePacket(@NotNull Packet<?> packet, Consumer<Packet<?>> consumer) {
                    consumer.accept(packet);
                }

                @Nullable
                public Bundler startPacketBundling(@NotNull Packet<?> splitter) {
                    return null;
                }
            };
            PacketBundleUnpacker packetbundleunpacker = new PacketBundleUnpacker(EMPTY);
            Field loginDisconnect = Connection.class.getDeclaredField("sendLoginDisconnect");
            loginDisconnect.setAccessible(true);
            loginDisconnect.set(networkManager, false);
            UnconfiguredPipelineHandler.InboundConfigurationTask unconfiguredpipelinehandler_b = UnconfiguredPipelineHandler.setupInboundProtocol(protocolInfo);
            PacketBundlePacker packetbundlepacker = new PacketBundlePacker(EMPTY);
            unconfiguredpipelinehandler_b = unconfiguredpipelinehandler_b.andThen((channelhandlercontext) -> {
                channelhandlercontext.pipeline().addAfter("decoder", "bundler", packetbundlepacker);
            });
            unconfiguredpipelinehandler_d = unconfiguredpipelinehandler_d.andThen((channelhandlercontext) -> {
                channelhandlercontext.pipeline().addAfter("encoder", "unbundler", packetbundleunpacker);
            });
            networkManager.channel.writeAndFlush(unconfiguredpipelinehandler_d);
            networkManager.channel.writeAndFlush(unconfiguredpipelinehandler_b);
        } catch (Throwable var9) {
            try {
                throw var9;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void join() {
        if (PlayersSpoof.instance.getConfig().getBoolean("enable-joins-message")) {
            String joinMessage = PlayersSpoof.instance.getConfig().getString("join-message");
            if (joinMessage != null) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', joinMessage)
                    .replace("%player%", name));
        }
    }

    @Override
    public void disconnect() {
        npc.connection.disconnect(Component.nullToEmpty("Disconnected!"));

        //server.getPlayerList().remove(npc);
        /*if (PlayersSpoof.instance.getConfig().getBoolean("enable-joins-message")) {
            String leaveMessage = PlayersSpoof.instance.getConfig().getString("leave-message");

            if (leaveMessage != null) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', leaveMessage
                    .replace("%player%", name)));
        }
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        ClientboundRemoveEntitiesPacket remove = new ClientboundRemoveEntitiesPacket(npc.getId());

        ClientboundPlayerInfoUpdatePacket infoRemove = new ClientboundPlayerInfoUpdatePacket(
                EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED), List.of(npc));
        serverPlayer.connection.send(infoRemove);
        serverPlayer.connection.send(remove);*/
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void spawn(Player player) {
        this.npc.absMoveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        //ClientboundAddEntityPacket spawn = new ClientboundAddEntityPacket(npc);
        ((CraftWorld)player.getWorld()).getHandle().addFreshEntity(npc, CreatureSpawnEvent.SpawnReason.CUSTOM);
        ClientboundRotateHeadPacket head = new ClientboundRotateHeadPacket(npc, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));

        if (visible) {
            ClientboundPlayerInfoUpdatePacket infoAdd = new ClientboundPlayerInfoUpdatePacket(
                    EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
                            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME), List.of(npc));
            sendPacket(player,infoAdd);
        }

        sendPacket(player, head);
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void setVisible(boolean visible) {
        if (visible == isVisible()) return;

        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setRank(String rank) {
        this.rank = rank;
    }

    @Override
    public String getRank() {
        return this.rank;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String changeName(String name) {
        this.name = name;
        return name;
    }
}