package net.parkwayschools.util;

import java.awt.geom.Area;
import java.io.File;
import java.io.PrintWriter;

public class Log {
    String _area;
    PrintWriter _pw;
    public Log(String area){
        _area = area;
        try {
            _pw = new PrintWriter(new File("log.txt"));
        } catch (Exception e){
            err("couldn't open own file!");
        }
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
        String s = String.format("[%s] [%s] %s\n",lvl,_area,msg);
        _pw.write(s);
        System.out.printf(s);
    }
}
