package NifData;

import Geometry.Face;
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



}