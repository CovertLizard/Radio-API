package com.github.covertlizard.radio.event;

import com.github.covertlizard.radio.MidiSound;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by CovertLizard on 8/2/2015.
 * Project Bukkit_Test
 */
@SuppressWarnings("all")
public class MidiLoadEvent extends Event implements Cancellable
{
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancel;
    private MidiSound sound;
    /**
     * Called when a sound is loaded into a station
     * @param sound the sound instance
     */
    public MidiLoadEvent(MidiSound sound)
    {
        this.sound = sound;
    }
    public MidiSound getSound()
    {
        return this.sound;
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