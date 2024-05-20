package com.spoofer.obj;

import com.mojang.authlib.GameProfile;
import com.spoofer.PlayersSpoof;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R4.CraftServer;
import org.bukkit.craftbukkit.v1_20_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class FakeEntity implements IFakeEntity {

    protected String name;
    protected boolean visible;
    protected String rank;
    protected String suffix;
    protected Location location;
    private ServerPlayer npc;


    public FakeEntity(String name, Location location){
        this(name, true, "default","", location);
    }

    public FakeEntity(String name, boolean visible, Location location){
        this(name, visible, "default","",location);
    }

    public FakeEntity(String name, boolean visible, String rank,String suffix, Location location){
        this.name = name;
        this.visible = visible;
        this.rank = rank;
        this.location = location;
        this.suffix = suffix;

    }

    public void create(){
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel worldServer = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);

        ClientInformation info = new ClientInformation("en_us", 0, ChatVisiblity.FULL, false, 0, HumanoidArm.RIGHT, false, true);
        this.npc = new ServerPlayer(server,worldServer, gameProfile, info);

        this.npc.listName = Component.empty().append(ChatColor
                .translateAlternateColorCodes('&',rank + " " + suffix  + name));

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(name);

        if(team == null){
            team = scoreboard.registerNewTeam(name);
        }

        //team.setPrefix(ChatColor.translateAlternateColorCodes('&',  rank + " "));
        //team.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&ahm"));

        team.addEntry(name);

        this.npc.setPos(location.getX(), location.getY(), location.getZ());
        npc.connection = new ServerGamePacketListenerImpl(server,new Connection(PacketFlow.CLIENTBOUND),npc,new CommonListenerCookie(gameProfile,0,info,true));

        //this.npc.getEntityData().set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
    }

    @Override
    public void join() {
        String joinMessage = PlayersSpoof.instance.getConfig().getString("join-message");
        if(joinMessage != null) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',joinMessage));
    }

    @Override
    public void leave() {
        String leaveMessage = PlayersSpoof.instance.getConfig().getString("leave-message");

        if (leaveMessage != null) Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',leaveMessage));

    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void spawn(Player player) {
        this.npc.absMoveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        ServerPlayer serverPlayer = ((CraftPlayer)player).getHandle();

        ClientboundAddEntityPacket spawn = new ClientboundAddEntityPacket(npc);
        ClientboundRotateHeadPacket head = new ClientboundRotateHeadPacket(npc, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));

        if(visible) {
            ClientboundPlayerInfoUpdatePacket infoAdd = new ClientboundPlayerInfoUpdatePacket(
                    EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
                            ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME), List.of(npc));
            serverPlayer.connection.send(infoAdd);
        }

        serverPlayer.connection.send(spawn);
        serverPlayer.connection.send(head);
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void setVisible(boolean visible) {
        if(visible == isVisible()) return;

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
