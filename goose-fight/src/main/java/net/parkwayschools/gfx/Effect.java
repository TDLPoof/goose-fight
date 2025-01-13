package net.parkwayschools.gfx;

import net.parkwayschools.phys.Vector2;

public class Effect {
    public String sheetName;
    public String animName;
    public int animFrames;
    int _lifetime;
    EffectType _t;
    int x,y;

    public enum EffectType {
        STATIONARY,
        SCREENSHAKE
    }

    public Effect(int x, int y, String sheet, String spr, int frames, EffectType t){
        this.x = x;
        this.y = y;
        sheetName = sheet;
        animName = spr;
        animFrames = frames;
        _lifetime = animFrames;
        this._t = t;
    }
    public static Effect global(EffectType e, int frames){
        return new Effect(-1,-1,"$GlobalFX","",frames,e);
    }
    public void tick(){
        _lifetime--;
        switch (_t){
            case STATIONARY -> {}
        }
    }
    public boolean over(){return _lifetime <= 0;}
    public RenderObj ro(){
        return new RenderObj(new Vector2(x,y),sheetName,animName,true,animFrames);
    }
}
