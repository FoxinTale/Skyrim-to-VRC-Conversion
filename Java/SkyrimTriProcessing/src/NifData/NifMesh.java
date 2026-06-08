package NifData;

import Geometry.Face;
import Geometry.Morph;
import Geometry.MorphVertex;
import Geometry.Vertex;

import java.util.ArrayList;

public class NifMesh {
    public String name;
    public int shapeBlockIndex;
    public ArrayList<Vertex> vertices;
    public ArrayList<Face> faces;

    public NifMesh(String name, int shapeBlockIndex,
                   ArrayList<Vertex> vertices,
                   ArrayList<Face> faces) {
        this.name = name;
        this.shapeBlockIndex = shapeBlockIndex;
        this.vertices = vertices;
        this.faces = faces;
    }

    public static NifMesh applyMorph(NifMesh baseMesh, Morph morph) {
        ArrayList<Vertex> morphedVertices = new ArrayList<>();

        for (Vertex v : baseMesh.vertices) {
            morphedVertices.add(new Vertex(v.x, v.y, v.z));
        }

        for (MorphVertex mv : morph.vertices) {
            int index = mv.index;

            if (index < 0 || index >= morphedVertices.size()) {
                continue;
            }

            Vertex v = morphedVertices.get(index);

            v.x += mv.x;
            v.y += mv.y;
            v.z += mv.z;
        }

        return new NifMesh(
                baseMesh.name + "_" + morph.name,
                baseMesh.shapeBlockIndex,
                morphedVertices,
                baseMesh.faces
        );
    }
}