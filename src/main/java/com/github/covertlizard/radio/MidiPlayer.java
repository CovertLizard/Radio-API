package com.github.covertlizard.radio;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sound.midi.*;
import java.io.IOException;
import java.util.*;

/**
 * Created by CovertLizard on 8/1/2015.
 * Project Radio-API
 * Used for playing midi files to Minecraft clients
 */
@SuppressWarnings("all")
public class MidiPlayer implements Receiver, MetaEventListener
{
    private final Map<Integer, Byte> patches = new HashMap<>();
    private final List<UUID> players = new ArrayList<>();
    private final MidiSound sound;

    private Sequencer sequencer;

    private boolean play, pause = false;

    private int task;

    public MidiPlayer(MidiSound sound)
    {
        if(sound == null) throw new IllegalArgumentException("The MidiSound instance cannot be NULL.");
        this.sound = sound;
    }
    //======================================================================
    //                        Class methods
    //======================================================================

    /**
     * Begins playing the midi file to the players
     * @param plugin the plugin instance
     * @param players the players to play to
     * @throws MidiUnavailableException
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    public void play(JavaPlugin plugin, Collection<Player> players) throws MidiUnavailableException, InvalidMidiDataException, IOException
    {
        if(this.play) throw new IllegalStateException("The file is already being played.");
        if(plugin == null) throw new IllegalArgumentException("Plugin cannot be NULL.");
        //todo throw midi play event
        players.forEach(player -> this.players.add(player.getUniqueId()));
        this.sequencer = this.sequencer == null ? MidiSystem.getSequencer() : this.sequencer;
        this.sequencer.addMetaEventListener(this);
        if(!this.sequencer.isOpen()) this.sequencer.open();
        this.sequencer.getTransmitters().forEach(transmitter -> transmitter.setReceiver(this));
        this.sequencer.setSequence(MidiSystem.getSequence(this.sound.getFile()));
        this.sequencer.start();

    }

    /**
     * Stops playing music
     */
    public void stop()
    {
        //todo throw stop event
        this.play = false;
        this.sequencer.stop();
        this.sequencer.close();
        this.patches.clear();
    }

    /**
     * Pauses/Unpauses the midi player
     * @param pause whether or not to pause
     */
    public void pause(boolean pause)
    {
        this.pause = pause;
    }
    //======================================================================
    //                        Inheritted methods
    //======================================================================
    @Override
    public void send(MidiMessage message, long timeStamp)
    {
        if(message instanceof ShortMessage && !this.pause && !this.players.isEmpty()) this.receive(((ShortMessage) message));
    }

    /**
     * A helper method for the 'send' inheritted method, specifically made to save on memory usage.
     * @param message the message instance
     */
    public void receive(ShortMessage message)
    {
        switch(message.getCommand())
        {
            case ShortMessage.NOTE_ON:
                this.players.forEach(uuid -> Bukkit.getServer().getPlayer(uuid).playSound(Bukkit.getServer().getPlayer(uuid).getLocation(), this.toSound(this.patches.containsKey(message.getChannel()) ? this.patches.get(message.getChannel()) : 1, message.getChannel()), message.getData2() / 127 == 0 ? 1 : message.getData2() / 127, this.toPitch((message.getData1() - 6) % 24)));
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

    @Override
    public void close(){}
    @Override
    public void meta(MetaMessage meta)
    {
        if(meta.getType() != 0x2F) return;
        System.out.println("Omg the song finished!");
    }
    //======================================================================
    //                          Utility Methods
    //======================================================================
    /**
     * Returns the correct sound to play
     * @param patch the patch
     * @param channel the channel
     * @return the sound instance
     */
    public Sound toSound(int patch, int channel)
    {
        return channel == 9 || patch >= 13 && patch <= 119 ? Sound.NOTE_BASS_DRUM : patch > 28 && patch <= 40 || patch >= 44 && patch <= 46 ? Sound.NOTE_BASS_GUITAR : patch >= 120 && patch <= 127 ? Sound.NOTE_SNARE_DRUM : Sound.CLICK;
    }

    /**
     * Returns the pitch
     * @param note the note
     * @return the pitch
     */
    public float toPitch(int note)
    {
        switch(note)
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
    //                            Getters
    //======================================================================
    public Map<Integer, Byte> getPatches()
    {
        return this.patches;
    }
    public List<UUID> getPlayers()
    {
        return this.players;
    }
    public MidiSound getSound()
    {
        return this.sound;
    }
    public Sequencer getSequencer()
    {
        return this.sequencer;
    }
    public boolean isPlaying()
    {
        return this.play;
    }
    public boolean isPaused()
    {
        return this.pause;
    }
}