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

            NifSkinInstance skin = NifSkinInstance.readSkinInstance(nif, shape.skinInstanceRef);
            NiSkinPartition partition = NiSkinPartition.readSkinPartition(nif, skin.skinPartitionRef);
            ArrayList<Vertex> baseVertices = chooseExportVertices(partition);
            ArrayList<UV> baseUvs = chooseExportUvs(partition);

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
                        partition.faces,
                        baseUvs
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
                System.out.println(" Wrote " + morph.name + " for shape " + triShape.name + ".");
            }
            System.out.println(" "+ triShape.name + " extracted");
        }
        System.out.println(" Extraction Complete");
    }

    private static ArrayList<Vertex> chooseExportVertices(NiSkinPartition partition) {
        int maxFaceIndex = getMaxFaceIndex(partition.faces);

        if (partition.objVertices != null
                && !partition.objVertices.isEmpty()
                && maxFaceIndex < partition.objVertices.size()) {
            return partition.objVertices;
        }

        return partition.vertices;
    }

    private static ArrayList<UV> chooseExportUvs(NiSkinPartition partition) {
        int maxFaceIndex = getMaxFaceIndex(partition.faces);

        if (partition.objUvs != null
                && !partition.objUvs.isEmpty()
                && maxFaceIndex < partition.objUvs.size()) {
            return partition.objUvs;
        }

        return partition.uvs;
    }

    private static int getMaxFaceIndex(ArrayList<Face> faces) {
        int max = -1;

        for (Face f : faces) {
            max = Math.max(max, f.a);
            max = Math.max(max, f.b);
            max = Math.max(max, f.c);
        }

        return max;
    }

    private static String safeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
