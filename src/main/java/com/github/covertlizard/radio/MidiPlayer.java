package com.github.covertlizard.radio;

import com.github.covertlizard.radio.event.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sound.midi.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CovertLizard on 8/1/2015.
 * Project Bukkit_Test
 */
@SuppressWarnings("all")
public class MidiPlayer implements MetaEventListener, Receiver
{
    private final JavaPlugin plugin;
    private final Map<Integer, Byte> patches = new HashMap<>();
    private final Map<Integer, MidiSound> sounds = new HashMap<>();
    private Sequencer sequencer;
    private MidiSound sound;

    private boolean pause, play = false;

    /**
     * Used for playing MIDI files
     * @param plugin the plugin instance
     */
    public MidiPlayer(JavaPlugin plugin)
    {
        if(plugin == null) throw new IllegalArgumentException("Plugin cannot be NULL.");
        this.plugin = plugin;
        try
        {
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.addMetaEventListener(this);
            this.sequencer.getTransmitters().forEach(transmitter -> transmitter.setReceiver(this));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            Bukkit.getServer().getLogger().warning("An error occured while trying to load the MidiPlayer.");
        }
    }
    //======================================================================
    //                        Class methods
    //======================================================================

    /**
     * Loads a sound into the player
     * @param sound the sound to load
     * @param id the sound's id
     */
    public void load(MidiSound sound, int id)
    {
        if(!this.throwEvent(new MidiLoadEvent(sound))) return;
        this.sounds.put(id, sound);
    }

    /**
     * Removes a sound from the player
     * @param id the sound id
     */
    public void remove(int id)
    {
        if(this.sounds.containsKey(id)) this.sounds.remove(id);
    }

    /**
     * Prevents the player from going to the next note
     * @param pause whether or not to pause the MIDI file
     */
    public void pause(boolean pause)
    {
        if(this.sound == null) return;
        if(!this.throwEvent(new MidiPauseEvent(this.sound, pause))) return;
        this.pause = pause;
    }

    /**
     * Stops the sound from playing
     */
    public void stop()
    {
        if(!this.play) return;
        if(!this.throwEvent(new MidiStopEvent(sound))) return;
        this.sequencer.stop();
        this.sequencer.close();
        this.patches.clear();
    }

    /**
     * Plays the sound to the select group of players
     * @param id the id of the sound to play
     * @param players the players to listen
     */
    public void play(int id, Collection<Player> players)
    {
        if(this.play) throw new IllegalStateException("A file is already being played.");
        if(!this.sounds.containsKey(id)) throw new IllegalArgumentException("The sound with the id of " + id + " cannot be found.");
        if(!this.throwEvent(new MidiPlayEvent(this.sounds.get(id), id, players))) return;
        players.forEach(player -> this.sounds.get(id).tune(player, true));
        this.sound = this.sounds.get(id);
        try
        {
            this.sequencer.setSequence(MidiSystem.getSequence(sound.getFile()));
            if(!this.sequencer.isOpen()) this.sequencer.open();
        }
        catch (InvalidMidiDataException | IOException | MidiUnavailableException exception)
        {
            exception.printStackTrace();
            if(exception instanceof InvalidMidiDataException)
            {
                Bukkit.getServer().getLogger().warning("Could not parse the midi file for: " + this.sound.getFile().getName());
                return;
            }
            if(exception instanceof IOException)
            {
                Bukkit.getServer().getLogger().warning("Could not load file: " + this.sound.getFile().getName());
                return;
            }
            if(exception instanceof MidiUnavailableException)
            {
                Bukkit.getServer().getLogger().warning("Could not load file: " + this.sound.getFile().getName() + ", perhaps it's already in use or reached resource limit.");
                return;
            }
        }
        this.sequencer.start();
    }

    /**
     * Plays the sound to the select group of players
     * @param id the id of the sound to play
     * @param players the players to listen
     */
    public void play(int id, Player... players)
    {
        this.play(id, Arrays.asList(players));
    }

    /**
     * Tunes the player in or out of the current MIDI sound
     * @param player the player
     * @param tune 'true' to tune in and 'false' to tune out
     */
    public void tune(Player player, boolean tune)
    {
        if(!this.throwEvent(new MidiTuneEvent(player, tune))) return;
        if(this.sound != null) this.sound.tune(player, tune);
    }

    /**
     * Determines if the player is tuned into the current MIDI sound
     * @param player the player
     * @return 'true' if they're tuned in
     */
    public boolean tuned(Player player)
    {
        return this.sound == null ? false : this.sound.tuned(player);
    }

    //======================================================================
    //                        Inheritted methods
    //======================================================================

    /**
     * Called when the listener receives a MetaMessage
     * @param meta the MetaMessage
     */
    @Override public void meta(MetaMessage meta)
    {
        if(meta.getType() != 0x2F) return;
        this.stop();
    }

    /**
     * Called when the listener is sent a command message
     * @param message the command message
     * @param timeStamp the time stamp
     */
    @Override
    public void send(MidiMessage message, long timeStamp)
    {
        if(message instanceof ShortMessage && !this.pause && !this.sound.getPlayers().isEmpty()) this.receive(((ShortMessage) message));
    }

    /**
     * A helper method for the 'send' inheritted method, specifically made to save on memory usage.
     * @param message the message instance
     */
    private void receive(ShortMessage message)
    {
        switch(message.getCommand())
        {
            case ShortMessage.NOTE_ON:
                this.sound.getPlayers().forEach(uuid -> Bukkit.getServer().getPlayer(uuid).playSound(Bukkit.getServer().getPlayer(uuid).getLocation(), this.toSound(this.patches.containsKey(message.getChannel()) ? this.patches.get(message.getChannel()) : 1, message.getChannel()), message.getData2() / 127 == 0 ? 1 : message.getData2() / 127, this.toPitch((message.getData1() - 6) % 24)));
                return;
            case ShortMessage.PROGRAM_CHANGE:
                this.patches.put(message.getChannel(), (byte) message.getData1());
                return;
            case ShortMessage.STOP:
                this.stop();
                break;
            default:
                return;
        }
    }

    /**
     * Called when it closes
     */
    @Override
    public void close(){}

    //======================================================================
    //                          Utility Methods
    //======================================================================

    /**
     * Throws an event
     * @param event the event instance
     * @return true if it wasn't cancelled
     */
    private <T extends Event & Cancellable> boolean throwEvent(T event)
    {
        this.plugin.getServer().getPluginManager().callEvent(event);
        return !event.isCancelled();
    }
    /**
     * Returns the correct sound to play
     * @param patch the patch
     * @param channel the channel
     * @return the sound instance
     */
    public Sound toSound(int patch, int channel)
    {
        return channel == 9 ? Sound.NOTE_BASS_DRUM : patch >= 28 && patch <= 40 || patch >= 44 && patch <= 46 ? Sound.NOTE_BASS_GUITAR : patch >= 113 && patch <= 119 ? Sound.NOTE_BASS_DRUM : patch >= 120 && patch <= 127 ? Sound.NOTE_SNARE_DRUM : Sound.NOTE_PIANO;
    }

    /**
     * Returns the pitch
     * @param note the note
     * @return the pitch
     */
    public float toPitch(int note)
    {
        switch (note)
        {
            case 1:
                return 0.53F;
            case 2:
                return 0.56F;
            case 3:
                return 0.6F;
            case 4:
                return 0.63F;
            case 5:
                return 0.67F;
            case 6:
                return 0.7F;
            case 7:
                return 0.76F;
            case 8:
                return 0.8F;
            case 9:
                return 0.84F;
            case 10:
                return 0.9F;
            case 11:
                return 0.94F;
            case 12:
                return 1F;
            case 13:
                return 1.06F;
            case 14:
                return 1.12F;
            case 15:
                return 1.18F;
            case 16:
                return 1.26F;
            case 17:
                return 1.34F;
            case 18:
                return 1.42F;
            case 19:
                return 1.5F;
            case 20:
                return 1.6F;
            case 21:
                return 1.68F;
            case 22:
                return 1.78F;
            case 23:
                return 1.88F;
            case 24:
                return 2F;
            default:
                return 0.5F;
        }
    }
    //======================================================================
    //                          Getter Methods
    //======================================================================
    public JavaPlugin getPlugin()
    {
        return this.plugin;
    }
    public Map<Integer, Byte> getPatches()
    {
        return this.patches;
    }
    public Map<Integer, MidiSound> getSounds()
    {
        return this.sounds;
    }
    public Sequencer getSequencer()
    {
        return this.sequencer;
    }
    public MidiSound getCurrentSound()
    {
        return this.sound;
    }
    public boolean isPaused()
    {
        return this.pause;
    }
    public boolean isPlaying()
    {
        return this.play;
    }
}