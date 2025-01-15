package net.parkwayschools.gfx;

public record HMZone(int sx, int ex, int height){
    @Override
    public int sx() {
        return sx;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public int ex() {
        return ex;
    }
}