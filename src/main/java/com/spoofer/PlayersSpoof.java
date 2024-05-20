package com.spoofer;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.spoofer.manager.FakePlayerManager;
import com.spoofer.obj.FakePlayer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;
import java.util.Random;

public class PlayersSpoof extends JavaPlugin implements Listener {

    private FakePlayerManager fakePlayerManager;
    private static LuckPerms luckPerms;
    public static PlayersSpoof instance;
    @Override
    public void onEnable() {
        instance = this;

        try {
            if(!getDataFolder().exists())
                getDataFolder().mkdir();

            if (!new File(getDataFolder(), "config.yml").exists()) copyStreamToFile(getResource("config.yml"), new File(getDataFolder(), "config.yml"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.fakePlayerManager = new FakePlayerManager(this);
        getServer().getPluginManager().registerEvents(this, this);
        fakePlayerManager.setMultiplier(getConfig().getInt("multiplier"));
        fakePlayerManager.setJoinInterval(getConfig().getInt("join-interval"));

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            luckPerms = provider.getProvider();
        }

        List<String> ranks = getConfig().getStringList("ranks");

        for (int i = 0; i < 25; i++) {
            Random random = new Random();
            String rankStr = ranks.get(random.nextInt(ranks.size()));

            Group group = getLuckPerms().getGroupManager().getGroup(rankStr);
            if(group == null) continue;

            String rank = group.getCachedData().getMetaData().getPrefix();
            String suffix = group.getCachedData().getMetaData().getSuffix();

            FakePlayer fakePlayer = new FakePlayer("FP" + i,true,rank, suffix,Bukkit.getWorlds().get(0).getSpawnLocation());
            fakePlayerManager.addFakeEntity(fakePlayer);

            fakePlayer.setRank(rank);

            fakePlayer.create();
        }
    }

    public void copyStreamToFile(InputStream source, File destination) throws IOException {
        try (OutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = source.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } finally {
            if (source != null) {
                source.close();
            }
        }
    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

    /*@EventHandler
    public void onSneak(PlayerToggleSneakEvent event){
        FakePlayer fakePlayer = new FakePlayer("FakePlayer", event.getPlayer().getLocation());
        fakePlayerManager.addFakeEntity(fakePlayer);
        fakePlayer.spawn(event.getPlayer());
    }*/

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        fakePlayerManager.spawnFakePlayers(event.getPlayer());
    }

    public FakePlayerManager getFakePlayerManager() {
        return fakePlayerManager;
    }
    @EventHandler
    public void onPing(PaperServerListPingEvent event){
        System.out.println("event triggered");
        event.setNumPlayers(Bukkit.getOnlinePlayers().size() + fakePlayerManager.getFakeEntities().size());
    }
}