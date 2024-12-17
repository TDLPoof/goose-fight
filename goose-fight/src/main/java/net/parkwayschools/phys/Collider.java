public class Collider {
    public Vector2 position, size;
    public boolean isHurtbox;


    public boolean myStartOverlapsX;
    public boolean myStartOverlapsY;
    public boolean myEndOverlapsX;
    public boolean myEndOverlapsY;

    public Collider(double x, double y, double width, double height) {
        position = new Vector2(x, y);
        size = new Vector2(width, height);
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

    public boolean intersects(Collider other) {
        return horiIntersects(other) && vertIntersects(other);
    }
}