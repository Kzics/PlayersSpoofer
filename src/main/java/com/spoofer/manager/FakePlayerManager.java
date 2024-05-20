package com.spoofer.manager;

import com.spoofer.PlayersSpoof;
import com.spoofer.obj.FakeEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class FakePlayerManager {

    private final List<FakeEntity> fakeEntities;
    private double multiplier;
    private int joinInterval;
    private final PlayersSpoof playersSpoof;

    public FakePlayerManager(PlayersSpoof playersSpoof){
        this.fakeEntities = new ArrayList<>();
        this.playersSpoof = playersSpoof;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public void setJoinInterval(int joinInterval) {
        this.joinInterval = joinInterval;
    }

    public void createFakePlayers(Location location) {
        int realPlayers = Bukkit.getOnlinePlayers().size();
        int fakePlayersToCreate = (int) Math.round(realPlayers * multiplier);
        for (int i = 0; i < fakePlayersToCreate; i++) {
            FakeEntity fakePlayer = new FakeEntity("FakePlayer" + i, location);
            String rank = PlayersSpoof.getLuckPerms().getGroupManager().getGroup(fakePlayer.getRank()).getDisplayName();
            fakePlayer.changeName(rank + " " + fakePlayer.getName());

            fakeEntities.add(fakePlayer);
        }
    }

    public void spawnFakePlayers(Player player) {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index < fakeEntities.size()) {
                    FakeEntity fakePlayer = fakeEntities.get(index++);
                    fakePlayer.join();
                    fakePlayer.spawn(player);
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(playersSpoof, 0, joinInterval * 20L);
    }

    public int getFakePlayerCount() {
        return fakeEntities.size();
    }
    public void addFakeEntity(FakeEntity fakeEntity){
        fakeEntities.add(fakeEntity);
    }

    public void removeFakeEntity(FakeEntity fakeEntity){
        fakeEntities.remove(fakeEntity);
        fakeEntity.leave();
    }

    public List<FakeEntity> getFakeEntities(){
        return fakeEntities;
    }
}
