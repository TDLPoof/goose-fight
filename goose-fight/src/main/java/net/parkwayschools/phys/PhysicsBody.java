package net.parkwayschools.phys;
import java.util.ArrayList;

public class PhysicsBody
{
    public static final Vector2 GRAVITY = new Vector2(0, 0.98);

    public double mass, drag, restitution;
    public Vector2 position;
    public Vector2 velocity, acceleration;

    public Collider collider;
    public ArrayList<Collider> collisionObjects;

    public PhysicsBody(double x, double y, double width, double height, double m, double d, double r) {
        collisionObjects = new ArrayList<>();
        position = new Vector2(x, y);
        collider = new Collider(x, y, width, height);
        velocity = Vector2.zero;
        acceleration = Vector2.zero;
        mass = m;
        drag = d;
        restitution = r;
    }

    public void update() {
        position.add(velocity);
        collider.position = new Vector2(position.x, position.y);
        for (Collider c : collisionObjects) {
            if (collider == c) continue;
            if (c.isHurtbox) continue;
            if (collider.intersects(c)) {
                position.sub(velocity);
                collider.position = new Vector2(position.x, position.y);

                if (collider.horiIntersects(c)) velocity = new Vector2(velocity.x * -restitution, velocity.y);
                if (collider.vertIntersects(c)) velocity = new Vector2(velocity.x, velocity.y * -restitution);
            }
        }
        velocity.add(acceleration);
        velocity.add(GRAVITY);
    }

    public void addForce(Vector2 force) {velocity.add(new Vector2(force.x / mass, force.y / mass)); }
}