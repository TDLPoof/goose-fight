package net.parkwayschools.snd;

import javax.sound.sampled.*;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import net.parkwayschools.util.Log;

public class SndMgr {
    Log l = new Log("sound");
    String _currentBGM;
    HashMap<String,SndAsset> _sounds;

    public SndMgr(){
        _currentBGM = "bgm.none";
        _sounds = new HashMap<>();
        try {
            loadSounds();
        } catch (Exception e){
            l.err("Failed to load audio files!");
            l.err(e.toString());
        }
    }

    void loadSounds() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        ArrayList<SndAsset> result = new ArrayList<>();
        Scanner sasha = new Scanner(new File("data/audio/snd.csv"));
        sasha.nextLine();
        while (sasha.hasNextLine()){
            String[] parts = sasha.nextLine().split(",");
            l.inf("Loading clip "+parts[0]+" from "+parts[1]);
            Clip c = AudioSystem.getClip();
            AudioInputStream a = AudioSystem.getAudioInputStream(new File("data/audio/"+parts[1]));
            float sampleRate = a.getFormat().getSampleRate();
            c.open(a);
            //id,name,type,loops,loopStart,loopEnd
            int lStart = Integer.parseInt(parts[4]);
            int lEnd = Integer.parseInt(parts[5]);
            if (parts[3].equals("true")){
                c.setLoopPoints((int) (lStart*(sampleRate/1000)), (int) (lEnd*(sampleRate/1000)));
            }
            _sounds.put(parts[0],new SndAsset(parts[0], SndType.fromStr(parts[2]), parts[3].equals("true"),lStart,lEnd,c));
        }
    }

    public void setBGM(String sndID){
        //stop the preceeding sfx clip, if it exists
        if (!_currentBGM.equals("bgm.none")) _sounds.get(_currentBGM).c().stop();
        _currentBGM = sndID;
        _sounds.get(_currentBGM).c().setFramePosition(0);
        _sounds.get(_currentBGM).c().loop(0); //0 = indefinite end
    }

    public void playSnd(String sndId){
        _sounds.get(sndId).c().setFramePosition(0);
        _sounds.get(sndId).c().start();
    }
}
