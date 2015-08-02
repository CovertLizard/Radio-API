package com.github.covertlizard.radio;

import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by CovertLizard on 8/1/2015.
 * Project Bukkit_Test
 */
@SuppressWarnings("all")
public class MidiSound
{
    private final File file;
    private final String title, author;
    private final List<UUID> players = new ArrayList<>();

    /**
     * Used for storing information about a MIDI file and the clients tuned into the sound
     * @param file the MIDI file
     * @param title the MIDI's title
     * @param author the MIDI's author
     */
    public MidiSound(File file, String title, String author)
    {
        this.file = file;
        this.title = title;
        this.author = author;
    }

    /**
     * Tunes the player in or out of the MIDI sound
     * @param player the player
     * @param tune 'true' to tune in and 'false' to tune out
     */
    public void tune(Player player, boolean tune)
    {
        if(tune && !this.tuned(player)) this.players.add(player.getUniqueId());
        if(!tune && this.tuned(player)) this.players.remove(player.getUniqueId());
    }

    /**
     * Determines if the player is tuned into this MIDI sound
     * @param player the player
     * @return 'true' if they're tuned in
     */
    public boolean tuned(Player player)
    {
        return this.players.contains(player.getUniqueId());
    }
    public File getFile()
    {
        return this.file;
    }
    public String getTitle()
    {
        return this.title;
    }
    public String getAuthor()
    {
        return this.author;
    }
    public List<UUID> getPlayers()
    {
        return this.players;
    }
}