package Core;

import Geometry.Face;
import Geometry.UV;
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

    public static void writePartition(
            File outFile,
            NiSkinPartition partition,
            String objectName
    ) throws IOException {

        ArrayList<Vertex> verts =
                !partition.objVertices.isEmpty()
                        ? partition.objVertices
                        : partition.vertices;

        ArrayList<UV> uvs =
                !partition.objUvs.isEmpty()
                        ? partition.objUvs
                        : partition.uvs;

        try (PrintWriter out = new PrintWriter(new FileWriter(outFile))) {
            write(out, objectName, verts, partition.faces, uvs);
        }
    }

    static void write(
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

    public static void write(
            PrintWriter out,
            String objectName,
            ArrayList<Vertex> vertices,
            ArrayList<Face> faces,
            ArrayList<UV> uvs
    ) {
        out.println("o " + objectName);

        for (Vertex v : vertices) {
            out.printf(Locale.US, "v %.6f %.6f %.6f%n", v.x, v.y, v.z);
        }

        boolean hasUvs =
                uvs != null
                        && uvs.size() == vertices.size();

        if (hasUvs) {

            for (UV uv : uvs) {
                out.printf(Locale.US, "vt %.6f %.6f%n", uv.u, uv.v);
            }
        }

        for (Face f : faces) {
            if (hasUvs) {
                out.println("f "
                        + (f.a + 1) + "/" + (f.a + 1) + " "
                        + (f.b + 1) + "/" + (f.b + 1) + " "
                        + (f.c + 1) + "/" + (f.c + 1));
            } else {
                out.println("f "
                        + (f.a + 1) + " "
                        + (f.b + 1) + " "
                        + (f.c + 1));
            }
        }
    }
}