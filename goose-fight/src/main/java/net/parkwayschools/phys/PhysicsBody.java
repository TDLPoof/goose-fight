package net.parkwayschools.phys;

import java.util.ArrayList;

public class PhysicsBody
{
    public static final Vector2 GRAVITY = new Vector2(0, 0.98);
    public static final Vector2 CROUCH_GRAVITY = new Vector2(0,0.98*1.5);

    public double mass, restitution;
    public Vector2 position;
    public Vector2 velocity, acceleration;
    public Vector2 drag;
    public Vector2 groundedDrag;
    public int jumps = 2;
    public int walljumps = 2;

    public boolean grounded = false, walled = false;
    public boolean groundedLastFrame;
    public boolean crouching = false;
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
        groundedDrag = new Vector2(Math.sqrt(1 - (d.x - 1) * (d.x - 1)), d.y);
        restitution = r;
    }

    public void update() {
        groundedLastFrame = grounded;
        grounded = false;
        walled = false;
        position.add(velocity);
        collider.position = new Vector2(position.x, position.y);
        for (Collider c : collisionObjects) {
            if (collider == c) continue;
            if (c.isHurtbox) continue;

            if (collider.intersects(c)) {
                if (collider.vertIntersects(c)) {
                    position.y -= velocity.y;
                    velocity.y *= -restitution;
                    collider.position = new Vector2(position.x + velocity.x, position.y);
                    grounded = true;
                    jumps = 2;
                    walljumps = 2;
                  //  if (!collider.name.equals("tBox")) System.out.println("Vertical Intersection Detected [" + collider.name + " | " + c.name + "]");
                }
                if (collider.horiIntersects(c)) {
                    position.x -= velocity.x;
                    velocity.x *= -restitution;
                    collider.position = new Vector2(position.x + velocity.x, position.y);
                    //if (!collider.name.equals("tBox")) System.out.println("Horizontal Intersection Detected [" + collider.name + " | " + c.name + "]");
                    walled = true;
                }
            }
        }
        velocity.add(acceleration);
        velocity.add(crouching ? CROUCH_GRAVITY : GRAVITY);
        if (jumps >= 2) velocity.mult(new Vector2(1 - groundedDrag.x, 1 - groundedDrag.y));
        else velocity.mult(new Vector2(1 - drag.x, 1 - drag.y));
    }

    public void addForce(Vector2 force) {velocity.add(new Vector2(force.x / mass, force.y / mass)); }

    public void crouch() {
        crouching = true;
        collider.crouch();
        position = new Vector2(position.x, position.y + collider.size.y);
    }

    public void uncrouch() {
        crouching = false;
        position = new Vector2(position.x, position.y - collider.size.y);
        collider.uncrouch();
    }
}
