package Tri;

import Geometry.Face;
import Geometry.FaceGenMorph;
import Geometry.UV;
import Geometry.Vertex;

import java.util.ArrayList;

public class FaceGenTriData {
    public ArrayList<Vertex> vertices;
    public ArrayList<Face> faces;
    public ArrayList<UV> uvs;
    public ArrayList<FaceGenMorph> morphs;

    public FaceGenTriData(
            ArrayList<Vertex> vertices,
            ArrayList<Face> faces,
            ArrayList<UV> uvs,
            ArrayList<FaceGenMorph> morphs
    ) {
        this.vertices = vertices;
        this.faces = faces;
        this.uvs = uvs;
        this.morphs = morphs;
    }
}
