package net.parkwayschools.snd;

import javax.sound.sampled.Clip;

public record SndAsset(String id, SndType t, boolean loops, long lStart, long lEnd, Clip c) {

}
