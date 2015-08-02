package com.github.covertlizard.radio.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by CovertLizard on 8/2/2015.
 * Project Bukkit_Test
 */
@SuppressWarnings("all")
public class MidiTuneEvent extends Event implements Cancellable
{
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancel;
    private Player player;
    private boolean tune;
    /**
     * Called when a sound is loaded into a station
     * @param sound the sound instance
     */
    public MidiTuneEvent(Player player, boolean tune)
    {
        this.player = player;
        this.tune = tune;
    }
    public boolean isTuned()
    {
        return this.tune;
    }
    public Player getPlayer()
    {
        return this.player;
    }
    @Override public boolean isCancelled()
    {
        return this.cancel;
    }
    @Override public void setCancelled(boolean cancel)
    {
        this.cancel = cancel;
    }
    @Override
    public HandlerList getHandlers()
    {
        return handlerList;
    }
    public static HandlerList getHandlerList()
    {
        return handlerList;
    }
}