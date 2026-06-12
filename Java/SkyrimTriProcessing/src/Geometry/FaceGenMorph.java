package Geometry;

import java.util.ArrayList;

public class FaceGenMorph {
    public String name;
    public float scale;
    public ArrayList<MorphVertex> vertices;

    public FaceGenMorph(String name, float scale, ArrayList<MorphVertex> vertices) {
        this.name = name;
        this.scale = scale;
        this.vertices = vertices;
    }
}