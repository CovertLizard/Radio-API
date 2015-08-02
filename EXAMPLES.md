[Home](https://github.com/CovertLizard/Radio-API)
<br>
<h3><b>Before you can start playing MIDI files you're gonna have to instantiate and object from the MidiPlayer class, like so:</b></h3>
```
   MidiPlayer player = new MidiPlayer(MY_PLUGIN_INSTANCE);
```
<h3><b>After that you're gonna have to create an instance of the MidiSound class to hold all the MIDI file information, like so:</b></h3>
```
   MidiSound sound = new MidiSound(FILE_INSTANCE_HERE, MIDI_FILE_NAME, MIDI_FILE_AUTHOR);
```
<h3><b>Okay now let's load this MidiSound into the MidiPlayer instance we created before:</b></h3>
```
   player.load(sound, SOUND_ID_INTEGER); // Sound IDS are needed for the MidiPlayer to recognize which song is where
```
<h3><b>Now we want to actually play this sound to a player's client, you can do that like so:</b></h3>
```
   player.play(SOUND_ID_INTEGER, player1, player2, player3); // This is where that sound ID we used to load is needed
   // There are two ways you can play a sound:
   // 1. By typing in player instances (not to be confused with the MidiPlayer) separated by commas
   // 2. By inserting a Collection/List of players (not to be confused with the MidiPlayer) into the second parameter
```