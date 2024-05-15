package com.spoofer.obj;

import org.bukkit.Location;

public interface IFakeEntity {
    void join();
    void leave();
    void sendMessage(String message);
    void setVisible(boolean visible);
    boolean isVisible();
    void setRank(String rank);
    String getRank();
    String getName();
    String changeName(String name);
    Location getLocation();

}
