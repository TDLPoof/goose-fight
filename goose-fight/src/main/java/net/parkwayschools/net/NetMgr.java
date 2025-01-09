package net.parkwayschools.net;

import java.util.ArrayList;

public class NetMgr {
    final static String baseURL = "https://qsrv.luminoso.dev/advsw/";
    final static String devURL = "http://localhost:3000/advsw/";

    record Score(String name, int sc){}

    String _base;

    public NetMgr(boolean isDevEnv){
        _base = isDevEnv ? devURL : baseURL;
    }

    void submitHighscore(Score s){
        throw new UnsupportedOperationException();
    }

    ArrayList<Score> getHighscores(){
        throw new UnsupportedOperationException();
     //   return new ArrayList<>();
    }
}
