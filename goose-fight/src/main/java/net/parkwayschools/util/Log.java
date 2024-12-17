package net.parkwayschools.util;

import java.awt.geom.Area;

public class Log {
    String _area;
    public Log(String area){
        _area = area;
    }

    public void inf(String msg){
        _log("inf",msg);
    }
    public void wrn(String msg){
        _log("wrn",msg);
    }
    public void err(String msg){
        _log("err",msg);
    }
    public void dbg(String msg){
        _log("dbg",msg);
    }

    private void _log(String lvl, String msg){
        System.out.printf("[%s] [%s] %s\n",lvl,_area,msg);
    }
}
