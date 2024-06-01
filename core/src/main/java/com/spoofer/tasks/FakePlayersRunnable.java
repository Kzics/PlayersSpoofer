package com.spoofer.tasks;


import com.spoofer.PlayersSpoof;
import com.spoofer.obj.IFakeEntity;
import com.spoofer.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.List;
import java.util.Random;

public class FakePlayersRunnable extends BukkitRunnable {

    private final PlayersSpoof playersSpoof;

    public FakePlayersRunnable(PlayersSpoof playersSpoof) {
        this.playersSpoof = playersSpoof;
    }

    @Override
    public void run() {
        List<String> usernames;
        try {
            usernames = playersSpoof.getFakePlayerManager().fetchUsernames();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        int currentFakePlayers = playersSpoof.getFakePlayerManager().getFakeEntities().size();
        int realPlayers = Bukkit.getOnlinePlayers().size() - currentFakePlayers;
        int expectedFakePlayers = (int) Math.round(realPlayers * playersSpoof.getFakePlayerManager().getMultiplier());
        Location location = Bukkit.getWorld(PlayersSpoof.instance.getConfig().getString("bot-world")).getSpawnLocation();

        if (currentFakePlayers < expectedFakePlayers) {
            List<String> ranks = playersSpoof.getConfig().getStringList("ranks");
            IFakeEntity fakePlayer = NMSUtils.getFakeEntityInstance(usernames.get(new Random().nextInt(usernames.size())), true, location);
            String rank = PlayersSpoof.getLuckPerms().getGroupManager().getGroup(ranks.get(new Random().nextInt(ranks.size()))).getCachedData().getMetaData().getPrefix();
            fakePlayer.setRank(rank);

            fakePlayer.create();

            for (Player player : Bukkit.getOnlinePlayers()) fakePlayer.spawn(player);

            playersSpoof.getFakePlayerManager().addFakeEntity(fakePlayer);

        } else if(currentFakePlayers > expectedFakePlayers) {
            IFakeEntity fakePlayer = playersSpoof.getFakePlayerManager().getFakeEntities().get(0);
            fakePlayer.disconnect();
            playersSpoof.getFakePlayerManager().getFakeEntities().remove(fakePlayer);
        }
    }
}
