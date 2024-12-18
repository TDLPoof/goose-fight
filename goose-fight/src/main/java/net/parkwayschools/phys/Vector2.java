import java.text.DecimalFormat;

public class Vector2
{
    public static final Vector2 zero = new Vector2(0, 0);

    public double x, y;

    // constructors
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Vector2(double x) {
        this.x = x;
        this.y = x;
    }



    @Override public String toString() { return "Vector2(" + x + ", " + y + ")"; }

    public String toString(int dPlaces) {
        String awesome = "#.";
        for (int i = 0; i < dPlaces; i++) { awesome += "#"; }
        DecimalFormat formatter = new DecimalFormat(awesome);
        return "Vector2(" + formatter.format(x) + ", " + formatter.format(y) + ")";
    }

    public boolean equals(Vector2 other) {
        double tolerance = 1e-6;
        return Math.abs(x - other.x) < tolerance && Math.abs(y - other.y) < tolerance;
    }


    public double getAngle() { return Math.atan2(y, x); }
    public double getLength() { return Math.sqrt(x * x + y * y); }

    public void normalize() { x /= getLength(); y /= getLength(); }

    public double distanceSq(Vector2 other) { return (x-other.x) * (x-other.x) + (y-other.y) * (y-other.y); }
    public double distance(Vector2 other) { return Math.sqrt(distanceSq(other)); }

    public void add(Vector2 other) { x += other.x; y += other.y; }
    public void sub(Vector2 other) { x -= other.x; y -= other.y; }
    public void mult(Vector2 other) { x *= other.x; y *= other.y; }
    public void div(Vector2 other) { x /= other.x; y /= other.y; }
    public void mult(double scale) { x *= scale; y *= scale; }
    public void div(double scale) { x /= scale; y /= scale; }
}