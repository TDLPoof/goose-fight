package net.parkwayschools.gfx;

import net.parkwayschools.util.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class Spritesheet {
    static Log log = new Log("gfx/spritesheet");
    BufferedImage _sheet;
    SpritesheetMeta _meta;
    HashMap<String,BufferedImage> _registry;

    public BufferedImage spr(String id){
        return _registry.get(id);
    }

    public Spritesheet(String id){
        _registry = new HashMap<>();
        try {
            _meta = SpritesheetMeta.load(id);
        } catch (Exception e){
            log.err("Couldn't load metadata for "+id);
        }
        try {
            _sheet = ImageIO.read(new File(String.format("data/sprite/%s.png", id)));
        } catch (Exception e){
            log.err("Couldn't read the image for "+id);
        }
        for (int x = 0; x< _meta.tw(); x++){
            for (int y = 0; y<_meta.th(); y++){
                BufferedImage sprite = _sheet.getSubimage(x*_meta.spWidth(),y*_meta.spHeight(),_meta.spWidth(),_meta.spHeight());
                _registry.put(_meta.nameMap()[y][x],sprite);
            }
        }
    }
}
