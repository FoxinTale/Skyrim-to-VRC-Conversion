import Geometry.Face;
import Geometry.Vertex;
import NifData.NiSkinPartition;
import NifData.NifMesh;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;

public class ObjWriter {

    public static void writeMesh(File outFile, NifMesh mesh) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(outFile))) {
            write(out, mesh.name, mesh.vertices, mesh.faces);
        }
    }

    public static void writePartition(File outFile, NiSkinPartition partition, String objectName)
            throws IOException {

        ArrayList<Vertex> verts;

        if (partition.objVertices != null && !partition.objVertices.isEmpty()) {
            verts = partition.objVertices;
        } else {
            verts = partition.vertices;
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(outFile))) {
            write(out, objectName, verts, partition.faces);
        }
    }

    public static void write(
            PrintWriter out,
            String objectName,
            ArrayList<Vertex> vertices,
            ArrayList<Face> faces
    ) {
        out.println("o " + objectName);

        for (Vertex v : vertices) {
            out.printf(Locale.US, "v %.6f %.6f %.6f%n", v.x, v.y, v.z);
        }

        for (Face f : faces) {
            out.println("f "
                    + (f.a + 1) + " "
                    + (f.b + 1) + " "
                    + (f.c + 1));
        }
    }
}