package Geometry;

import java.util.List;

public class Morph {
    public String name;
    public float multiplier;
    public int changedCount;
    public List<MorphVertex> vertices;

    public Morph(String name, float multiplier, int changedCount, List<MorphVertex> vertices) {
        this.name = name;
        this.multiplier = multiplier;
        this.changedCount = changedCount;
        this.vertices = vertices;
    }
}