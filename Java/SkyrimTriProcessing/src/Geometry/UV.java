package Geometry;

public class UV {
    public float u;
    public float v;

    public UV(float u, float v) {
        this.u = u;
        this.v = v;
    }

    public static boolean looksLikeUv(float u, float v) {
        return !Float.isNaN(u)
                && !Float.isNaN(v)
                && u > -2.0f && u < 2.0f
                && v > -2.0f && v < 2.0f;
    }
}