public class Collider {
    public Vector2 position, size;
    public boolean isHurtbox;

    private boolean intersectsOW(Collider other) {
        boolean myStartOverlapsX = position.x > other.position.x && position.x < other.position.x + other.size.x;
        boolean myStartOverlapsY = position.y > other.position.y && position.y < other.position.y + other.size.y;

        boolean myEndOverlapsX = position.x + size.x > other.position.x && position.x + size.x < other.position.x + other.size.x;
        boolean myEndOverlapsY = position.y + size.y > other.position.y && position.y + size.y < other.position.y + other.size.y;

        return (myStartOverlapsX && myStartOverlapsY) || (myEndOverlapsX && myEndOverlapsY);
    }

    public boolean intersects(Collider other) {
        return intersectsOW(other) && other.intersectsOW(this);
    }
}