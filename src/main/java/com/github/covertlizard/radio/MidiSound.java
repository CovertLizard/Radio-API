package com.github.covertlizard.radio;

import java.io.File;

/**
 * Created by CovertLizard on 7/30/2015.
 * Project Radio-API
 * Any midi file you wish to play on a Minecraft client must be made into a MidiSound instance
 */
@SuppressWarnings("all")
public class MidiSound
{
    private final File file;
    private final String title;

    /**
     * This class is used as a holder for information about a midi file
     * @param file the midi file to play
     * @param title the midi file's title
     */
    public MidiSound(File file, String title)
    {
        this.file = file;
        this.title = title;
    }
    public File getFile()
    {
        return this.file;
    }
    public String getTitle()
    {
        return this.title;
    }
}