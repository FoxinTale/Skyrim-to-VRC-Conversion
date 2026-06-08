import Geometry.*;
import NifData.*;

import Readers.NifReader;
import Readers.TriReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static Geometry.TriShape.findTriShapeByName;


public class Extractor {
    public static void extractTriFile(File triFile, File nifFile, File outputFolder) throws IOException {
        TriData triData = TriReader.readTri(triFile);
        NifData nif = NifReader.readNif(nifFile);

        ArrayList<NifTriShape> shapes = NifReader.readTriShapes(nif);


        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        for (NifTriShape shape : shapes) {

            TriShape triShape = findTriShapeByName(triData, shape.name);

            if (triShape == null) {
                System.out.println("No TRI shape for " + shape.name);
                continue;
            }

            NifSkinInstance skin =
                    NifSkinInstance.readSkinInstance(nif, shape.skinInstanceRef);

            NiSkinPartition partition =
                    NiSkinPartition.readSkinPartition(nif, skin.skinPartitionRef);

            ArrayList<Vertex> baseVertices =
                    !partition.objVertices.isEmpty()
                            ? partition.objVertices
                            : partition.vertices;

            // Base mesh goes directly in root folder
            File baseFile = new File(
                    outputFolder,
                    safeFileName(shape.name) + ".obj"
            );

            try (PrintWriter out = new PrintWriter(new FileWriter(baseFile))) {
                ObjWriter.write(
                        out,
                        shape.name,
                        baseVertices,
                        partition.faces
                );
            }

            // Morphs go in per-shape folder
            File morphDir = new File(
                    outputFolder,
                    safeFileName(shape.name)
            );

            morphDir.mkdirs();

            for (Morph morph : triShape.morphs) {
                ArrayList<Vertex> morphedVertices =
                        Morph.applyMorphToVertices(
                                baseVertices,
                                morph
                        );

                File morphFile = new File(
                        morphDir,
                        safeFileName(morph.name) + ".obj"
                );

                try (PrintWriter out = new PrintWriter(new FileWriter(morphFile))) {
                    ObjWriter.write(
                            out,
                            morph.name,
                            morphedVertices,
                            partition.faces
                    );
                }
            }
        }
    }



    private static String safeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
