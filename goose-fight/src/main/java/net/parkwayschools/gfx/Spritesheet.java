package net.parkwayschools.gfx;

import net.parkwayschools.util.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

public class Spritesheet {
    static Log log = new Log("gfx/spritesheet");
    BufferedImage _sheet;
    public SpritesheetMeta meta;
    HashMap<String,BufferedImage> _registry;

    public BufferedImage spr(String id){
        return _registry.get(id);
    }

    public Spritesheet(String id){
        _registry = new HashMap<>();
        try {
            meta = SpritesheetMeta.load(id);
        } catch (Exception e){
            log.err("Couldn't load metadata for "+id);
            e.printStackTrace();
        }
        try {
            _sheet = ImageIO.read(new File(String.format("data/sprite/%s.png", id)));
            log.dbg("["+id+"]"+" has dimensions of "+_sheet.getWidth()+"x"+_sheet.getHeight());
        } catch (Exception e){
            log.err("Couldn't read the image for "+id);
        }
        for (int x = 0; x< meta.tw(); x++){
            for (int y = 0; y< meta.th(); y++){
                BufferedImage sprite = _sheet.getSubimage(x* meta.spWidth(),y* meta.spHeight(), meta.spWidth(), meta.spHeight());
                _registry.put(meta.nameMap()[y][x],sprite);
            }
        }
    }
}
