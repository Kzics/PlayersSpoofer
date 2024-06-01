package com.spoofer.obj;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IFakeEntity {
    void join();
    void disconnect();
    void sendMessage(String message);
    void setVisible(boolean visible);
    boolean isVisible();
    void setRank(String rank);
    String getRank();
    String getName();
    String changeName(String name);
    Location getLocation();
    void spawn(Player player);
    void create();

}