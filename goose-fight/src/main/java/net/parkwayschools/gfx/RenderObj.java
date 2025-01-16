package net.parkwayschools.gfx;

import net.parkwayschools.phys.Vector2;

public record RenderObj(Vector2 pos, String sheetID, String spriteID,
                        boolean isAnim, int animFrames, boolean renderShadow, boolean flipHorizontal, boolean shadowRespectsGroundplane, MagicAnim ma
                        ) {


    public RenderObj(Vector2 pos, String sheetID, String spriteID, boolean isAnim, int animFrames){
       this(pos,sheetID,spriteID,isAnim,animFrames,false,false,false, MagicAnim.NONE);
    }
    public RenderObj(Vector2 pos, String sheetID, String spriteID,
                     boolean isAnim, int animFrames, boolean renderShadow, boolean flipHorizontal){
        this(pos,sheetID,spriteID,isAnim,animFrames,renderShadow,flipHorizontal,true,MagicAnim.NONE);
    }
}
