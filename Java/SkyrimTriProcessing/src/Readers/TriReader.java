package Readers;

import Geometry.Morph;
import Geometry.MorphVertex;
import Tri.TriData;
import Tri.TriShape;

import java.io.*;
import java.util.*;

import static Readers.BinaryReader.*;

public class TriReader {

    public static TriData readTri(File path) throws IOException {
        List<TriShape> shapes = new ArrayList<>();
        boolean hasUvSection = false;

        try (InputStream in = new BufferedInputStream(new FileInputStream(path))) {
            byte[] magic = readExact(in, 4, "magic");

            if (magic[0] != 'P' || magic[1] != 'I' || magic[2] != 'R' || magic[3] != 'T') {
                throw new IOException("Not a BodySlide TRIP TRI file. Magic was: "
                        + Arrays.toString(magic));
            }

            int shapeCount = readU16(in, "shape_count");

            for (int i = 0; i < shapeCount; i++) {
                String shapeName = readStringU8(in, "shape_name");
                int morphCount = readU16(in, "morph_count");

                List<Morph> morphs = new ArrayList<>();

                for (int j = 0; j < morphCount; j++) {
                    String morphName = readStringU8(in, "morph_name");
                    float multiplier = readF32(in, "multiplier");
                    int changedCount = readU16(in, "changed_count");

                    List<MorphVertex> vertices = new ArrayList<>();

                    for (int k = 0; k < changedCount; k++) {
                        int vertexId = readU16(in, "vertex_id");

                        short xRaw = readI16(in, "x");
                        short yRaw = readI16(in, "y");
                        short zRaw = readI16(in, "z");

                        vertices.add(new MorphVertex(
                                vertexId,
                                xRaw * multiplier,
                                yRaw * multiplier,
                                zRaw * multiplier
                        ));
                    }

                    morphs.add(new Morph(
                            morphName,
                            multiplier,
                            changedCount,
                            vertices
                    ));
                }

                TriShape shape = new TriShape(shapeName);
                shape.morphs = morphs;

                shapes.add(shape);
            }

            hasUvSection = in.read() != -1 && in.read() != -1;
        }

        return new TriData(shapes, hasUvSection);
    }

}