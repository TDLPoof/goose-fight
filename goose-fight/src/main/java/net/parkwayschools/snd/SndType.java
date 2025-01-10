package net.parkwayschools.snd;

public enum SndType {
    BGM,
    SFX,
    GLEEP_GLORP;

    public static SndType fromStr(String s){
        return switch (s.toLowerCase()){
            case "bgm" -> BGM;
            case "bg" -> BGM;
            case "sfx" -> SFX;
            case "effect" -> SFX;
            default -> GLEEP_GLORP;
        };
    }
}
