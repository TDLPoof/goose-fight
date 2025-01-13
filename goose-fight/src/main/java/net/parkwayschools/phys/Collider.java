package net.parkwayschools.phys;

public class Collider {
    public Vector2 position, size;
    public boolean isHurtbox;
    public String name;

    public boolean myStartOverlapsX;
    public boolean myStartOverlapsY;
    public boolean myEndOverlapsX;
    public boolean myEndOverlapsY;

    public Collider(double x, double y, double width, double height) {
        position = new Vector2(x, y);
        size = new Vector2(width, height);
    }

    public Collider(double x, double y, double width, double height, String name) {
        position = new Vector2(x, y);
        size = new Vector2(width, height);
        this.name = name;
    }

    public Collider(double x, double y, double width, double height, boolean hurtbox) {
        position = new Vector2(x, y);
        size = new Vector2(width, height);
        isHurtbox = hurtbox;
    }

    private void updateIntersections(Collider other) {
        myStartOverlapsX = position.x > other.position.x && position.x < other.position.x + other.size.x;
        myStartOverlapsY = position.y > other.position.y && position.y < other.position.y + other.size.y;

        myEndOverlapsX = position.x + size.x > other.position.x && position.x + size.x < other.position.x + other.size.x;
        myEndOverlapsY = position.y + size.y > other.position.y && position.y + size.y < other.position.y + other.size.y;
    }

    public boolean horiIntersects(Collider other) {
        updateIntersections(other);
        return myStartOverlapsY || myEndOverlapsY;
    }
    public boolean vertIntersects(Collider other) {
        updateIntersections(other);
        return myStartOverlapsX || myEndOverlapsX;
    }

    private boolean subsumeInterval(double start1, double range1, double start2, double range2) {
        // 1 is small, 2 is big
        double end1 = start1 + range1;
        double end2 = start2 + range2;
        return start1 >= start2 && end1 <= end2;
    }

    public boolean goodHoriIntersects(Collider other) {
        return subsumeInterval(position.y, size.y, other.position.y, other.size.y) && horiIntersects(other);
    }

    public boolean goodVertIntersects(Collider other) {
        return subsumeInterval(position.x, size.x, other.position.x, other.size.x) && vertIntersects(other);
    }

    public boolean intersects(Collider other) {
        return horiIntersects(other) && vertIntersects(other);
    }

    public void crouch() {
        size = new Vector2(size.x, size.y / 2);
        position = new Vector2(position.x, position.y + size.y);
    }

    public void uncrouch() {
        position = new Vector2(position.x, position.y - size.y);
        size = new Vector2(size.x, size.y * 2);
    }
}
