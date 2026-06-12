package Readers;

import Geometry.*;
import Tri.FaceGenTriData;

import java.io.*;
import java.util.ArrayList;

public class FaceGenTriReader {
    public static FaceGenTriData read(File file) throws IOException {
        byte[] data = readAllBytes(file);
        BlockReader r = new BlockReader(data);

        String magic = r.readAscii(8);

        if (!magic.equals("FRTRI003")) {
            throw new IOException("Not a FaceGen TRI file. Magic was: " + magic);
        }

        int vertexCount = r.readI32();
        int triangleCount = r.readI32();
        int quadCount = r.readI32();
        int labelledVertexCount = r.readI32();
        int labelledSurfacePointCount = r.readI32();
        int textureCoordCount = r.readI32();
        int extensionInfo = r.readI32();
        int diffMorphCount = r.readI32();
        int statMorphCount = r.readI32();
        int statMorphVertexCount = r.readI32();

        r.skip(16); // reserved

        ArrayList<Vertex> vertices = new ArrayList<>();

        for (int i = 0; i < vertexCount; i++) {
            vertices.add(new Vertex(
                    r.readF32(),
                    r.readF32(),
                    r.readF32()
            ));
        }

        ArrayList<Face> faces = new ArrayList<>();

        for (int i = 0; i < triangleCount; i++) {
            faces.add(new Face(
                    r.readI32(),
                    r.readI32(),
                    r.readI32()
            ));
        }

        // Quads exist in spec, but Skyrim FaceGen usually has Q=0.
        if (quadCount > 0) {
            r.skip(quadCount * 4 * 4);
        }

        // Labelled vertices / surface points, not needed for OBJ export yet.
        // Leave as guarded skips until we map labels fully.
        if (labelledVertexCount > 0) {
            throw new IOException("Labelled vertices not supported yet: " + labelledVertexCount);
        }

        if (labelledSurfacePointCount > 0) {
            throw new IOException("Labelled surface points not supported yet: " + labelledSurfacePointCount);
        }

        ArrayList<UV> uvs = new ArrayList<>();

        for (int i = 0; i < textureCoordCount; i++) {
            float u = r.readF32();
            float v = r.readF32();

            uvs.add(new UV(u, 1.0f - v));
        }

        // ext == 1 appears to contain another triangle-sized int3 block.
        if (extensionInfo != 0) {
            r.skip(triangleCount * 3 * 4);
        }

        ArrayList<FaceGenMorph> morphs = new ArrayList<>();

        for (int m = 0; m < diffMorphCount; m++) {
            int nameLength = r.readI32();
            String name = r.readAscii(nameLength).trim();
            float scale = r.readF32();

            ArrayList<MorphVertex> deltas = new ArrayList<>();

            for (int i = 0; i < vertexCount; i++) {
                int dx = r.readI16();
                int dy = r.readI16();
                int dz = r.readI16();

                if (dx != 0 || dy != 0 || dz != 0) {
                    deltas.add(new MorphVertex(
                            i,
                            dx * scale,
                            dy * scale,
                            dz * scale
                    ));
                }
            }

            morphs.add(new FaceGenMorph(name, scale, deltas));
        }

        return new FaceGenTriData(
                vertices,
                faces,
                uvs,
                morphs
        );
    }

    private static byte[] readAllBytes(File file) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[8192];

            int read;

            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }

        return out.toByteArray();
    }
}
