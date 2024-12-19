package net.parkwayschools.phys;

import java.util.ArrayList;

public class PhysicsBody
{
    public static final Vector2 GRAVITY = new Vector2(0, 0.98);

    public double mass, restitution;
    public Vector2 position;
    public Vector2 velocity, acceleration;
    public Vector2 drag;
    public int jumps = 2;
    public int walljumps = 2;

    public boolean grounded = false, walled = false;

    public Collider collider;
    public ArrayList<Collider> collisionObjects;

    public PhysicsBody(double x, double y, double width, double height, double m, Vector2 d, double r, String n) {
        collisionObjects = new ArrayList<>();
        position = new Vector2(x, y);
        collider = new Collider(x, y, width, height, n);
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
            grounded = false;
            walled = false;

            if (collider.intersects(c)) {
                if (collider.goodHoriIntersects(c)) {
                    position.x -= velocity.x;
                    collider.position = new Vector2(position.x, position.y);
                    velocity.x *= -restitution;
            //        if (!collider.name.equals("tBox")) System.out.println("Horizontal Intersection Detected [" + collider.name + " | " + c.name + "]");
                    walled = true;
                }
                if (collider.goodVertIntersects(c)) {
                    position.y -= velocity.y;
                    collider.position = new Vector2(position.x, position.y);
                    grounded = true;
                    jumps = 2;
                    walljumps = 2;
                    velocity.y *= -restitution;
           //         if (!collider.name.equals("tBox")) System.out.println("Vertical Intersection Detected [" + collider.name + " | " + c.name + "]");
                }
            }
        }
        velocity.add(acceleration);
        velocity.add(GRAVITY);
        velocity.mult(new Vector2(1 - drag.x, 1 - drag.y));
    }

    public void addForce(Vector2 force) {velocity.add(new Vector2(force.x / mass, force.y / mass)); }
}