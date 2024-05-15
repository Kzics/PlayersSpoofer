package com.spoofer.obj;

import org.bukkit.Location;

public class FakeEntity implements IFakeEntity {

    protected String name;
    protected boolean visible;
    protected String rank;
    protected Location location;

    public FakeEntity(String name, Location location){
        this.name = name;
        this.visible = true;
        this.rank = null;
        this.location = location;
    }

    public FakeEntity(String name, boolean visible, Location location){
        this.name = name;
        this.visible = visible;
        this.rank = null;
        this.location = location;
    }

    public FakeEntity(String name, boolean visible, String rank, Location location){
        this.name = name;
        this.visible = visible;
        this.rank = rank;
        this.location = location;
    }

    @Override
    public void join() {

    }

    @Override
    public void leave() {

    }

    @Override
    public Location getLocation() {
        return location;
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
