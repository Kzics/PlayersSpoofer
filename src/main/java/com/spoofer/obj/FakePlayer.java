package com.spoofer.obj;

import org.bukkit.Location;

public class FakePlayer extends FakeEntity {
    public FakePlayer(String name, Location location) {
        super(name, location);
    }

    public FakePlayer(String name, boolean visible, Location location) {
        super(name, visible, location);
    }

    public FakePlayer(String name, boolean visible, String rank, String suffix,Location location) {
        super(name, visible, rank, suffix,location);
    }
}
