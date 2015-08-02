package com.github.covertlizard.radio.event;

import com.github.covertlizard.radio.MidiSound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Collection;

/**
 * Created by CovertLizard on 8/2/2015.
 * Project Bukkit_Test
 */
@SuppressWarnings("all")
public class MidiPlayEvent extends Event implements Cancellable
{
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancel;
    private MidiSound sound;
    private Collection<Player> players;
    private int id;
    /**
     * Called when a sound is loaded into a station
     * @param sound the sound instance
     */
    public MidiPlayEvent(MidiSound sound, int id, Collection<Player> players)
    {
        this.sound = sound;
        this.id = id;
        this.players = players;
    }
    public MidiSound getSound()
    {
        return this.sound;
    }
    public Collection<Player> getPlayers()
    {
        return players;
    }
    public int getId()
    {
        return id;
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