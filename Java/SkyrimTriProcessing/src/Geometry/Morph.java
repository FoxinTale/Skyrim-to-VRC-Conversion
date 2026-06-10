package Geometry;

import java.util.ArrayList;
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


    public static ArrayList<Vertex> applyMorphToVertices(
            ArrayList<Vertex> baseVertices,
            Morph morph
    ) {
        ArrayList<Vertex> result = new ArrayList<>();

        for (Vertex v : baseVertices) {
            result.add(new Vertex(v.x, v.y, v.z));
        }

        for (MorphVertex mv : morph.vertices) {
            if (mv.index < 0 || mv.index >= result.size()) {
                continue;
            }

            Vertex v = result.get(mv.index);

            v.x += mv.x;
            v.y += mv.y;
            v.z += mv.z;
        }

        return result;
    }

    public static int countMorphs(TriData triData) {
        int total = 0;

        for (TriShape shape : triData.shapes) {
            total += shape.morphs.size();
        }

        return total;
    }
}