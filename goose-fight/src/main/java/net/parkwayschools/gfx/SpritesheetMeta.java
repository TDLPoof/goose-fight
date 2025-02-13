package net.parkwayschools.gfx;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Metadata needed for loading a spritesheet file.
 * */
public record SpritesheetMeta(int spWidth, int spHeight,int th, int tw, String[][] nameMap, int xOff) {
    public static SpritesheetMeta load(String id) throws FileNotFoundException {
        File f = new File(String.format("data/sprite/%s.spmeta",id));
        Scanner fin = new Scanner(f);
        int sx = 0,sy = 0,th = 0,tw = 0;
        int xOff = 0;
        String[] mdLines = fin.nextLine().split(",");
        for (String s : mdLines){
            String[] kv = s.split("=");
            String[] dm = kv[1].split("x");
            switch (kv[0]){
                case "sprite" -> {
                    sx = Integer.parseInt(dm[0]);
                    sy = Integer.parseInt(dm[1]);
                }
                case "dim" -> {
                    tw = Integer.parseInt(dm[0]);
                    th = Integer.parseInt(dm[1]);
                }
                case "x-off" -> {
                    xOff = Integer.parseInt(dm[0]);
                }
            }
        }
        String[][] idMap = new String[th][tw];
        for (int i = 0; i<th; i++){
            String[] row = fin.nextLine().split(",");
            idMap[i] = row;
        }
        return new SpritesheetMeta(sx,sy,th,tw,idMap,xOff);
    }
}
