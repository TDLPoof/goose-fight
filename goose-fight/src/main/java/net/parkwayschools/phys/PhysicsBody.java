import java.util.ArrayList;

public abstract class PhysicsBody
{
    public double mass;
    public double drag;
    public Vector2 position;
    public Vector2 velocity, acceleration;
    public Vector2 gravity;

    public Collider collider;

    public void update() {
        position += velocity;
        collider.position = position;
        for (Collider c : GameManager.GetCollidersInGame()) {
            if (collider == c) continue;
            if (c.isHurtbox) continue;
            if (collider.intersects(c)) {
                position -= velocity;
                collider.position = position;
            }
        }
        velocity += acceleration + gravity;
    }

    public void addForce(Vector2 force) {acceleration += force.div(mass); }
}