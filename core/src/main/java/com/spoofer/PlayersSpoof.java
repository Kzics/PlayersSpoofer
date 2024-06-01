package com.spoofer;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.spoofer.commands.SpooferCommands;
import com.spoofer.manager.FakePlayerManager;
import com.spoofer.obj.IFakeEntity;
import com.spoofer.tasks.FakePlayersRunnable;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ServiceLoader;

public class PlayersSpoof extends JavaPlugin implements Listener {

    private FakePlayerManager fakePlayerManager;
    private static LuckPerms luckPerms;
    public static PlayersSpoof instance;
    @Override
    public void onEnable() {
        instance = this;
//3700 1.20.4 3839 : 1.20.6
        int packageName = Bukkit.getUnsafe().getDataVersion();
        System.out.println(packageName);
        try {
            if(!getDataFolder().exists())
                getDataFolder().mkdir();

            if (!new File(getDataFolder(), "config.yml").exists()) copyStreamToFile(getResource("config.yml"), new File(getDataFolder(), "config.yml"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println(Bukkit.getVersion());
        System.out.println(Bukkit.getBukkitVersion());
        this.fakePlayerManager = new FakePlayerManager(this);
        getServer().getPluginManager().registerEvents(this, this);
        fakePlayerManager.setMultiplier(getConfig().getInt("multiplier"));
        fakePlayerManager.setJoinInterval(getConfig().getInt("join-interval"));
        getCommand("spoofer").setExecutor(new SpooferCommands(this));

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider != null) {
            luckPerms = provider.getProvider();
        }
        new FakePlayersRunnable(this).runTaskTimer(this, 0, fakePlayerManager.getJoinInterval()*20L);

        /*for (int i = 0; i < 25; i++) {
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
        }*/
    }

    @Override
    public void onDisable() {
        fakePlayerManager.getFakeEntities().forEach(IFakeEntity::disconnect);
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
        for (IFakeEntity fakePlayer : fakePlayerManager.getFakeEntities()) {
            fakePlayer.spawn(event.getPlayer());
        }
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