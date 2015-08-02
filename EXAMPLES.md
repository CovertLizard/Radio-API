[Home](https://github.com/CovertLizard/Radio-API)
<br>
<h4><b>Before you can start playing MIDI files you're gonna have to instantiate and object from the MidiPlayer class:</b></h4>
```
   MidiPlayer player = new MidiPlayer(MY_PLUGIN_INSTANCE);
```
<h4><b>After that you're going to need to instantiate an object from the MidiSound class to hold all the MIDI file information:</b></h4>
```
   MidiSound sound = new MidiSound(FILE_INSTANCE_HERE, MIDI_FILE_NAME, MIDI_FILE_AUTHOR);
```
<h4><b>Okay now let's load this MidiSound into the MidiPlayer instance we created before:</b></h4>
```
   player.load(sound, SOUND_ID_INTEGER); // Sound IDS are needed for the MidiPlayer to recognize which song is where
   // you can remove a sound by typing:
   // player.remove(SOUND_ID_INTEGER);
```
<h4><b>Now we want to actually play this sound to a player's client, you can do that like so:</b></h4>
```
   player.play(SOUND_ID_INTEGER, player1, player2, player3); // This is where that sound ID we used to load is needed
   // There are two ways you can play a sound:
   // 1. By typing in player instances (not to be confused with the MidiPlayer) separated by commas
   // 2. By inserting a Collection/List of players (not to be confused with the MidiPlayer) into the second parameter
```
<h4><b>If you want to let another player listen to the music even if you already started playing it you can tune them in:</b></h4>
```
   player.tune(PLAYER_INSTANCE, true); // (PLAYER_INSTANCE is not to be confused with the MidiPlayer object)
   // If you change the boolean at the end to 'false' then it will tune them out from listening to the sound
```