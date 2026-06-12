package Core;

import Geometry.*;
import Lang.Localisation;
import Lang.Strings;
import NifData.*;

import Readers.FaceGenTriReader;
import Readers.NifReader;
import Readers.TriReader;
import Tri.FaceGenTriData;
import Tri.TriData;
import Tri.TriShape;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static Tri.TriShape.findTriShapeByName;


public class Extractor {
    private static final Strings strings = Localisation.getStrings();


    public static void extractFaceGenTriFile(File triFile, File outputFolder) throws IOException {
        FaceGenTriData facegenTri = FaceGenTriReader.read(triFile);
        outputFolder.mkdirs();

        File baseFile = new File(outputFolder, stripExtension(triFile.getName()) +  "_base.obj");

        try (PrintWriter out = new PrintWriter(new FileWriter(baseFile))) {
            ObjWriter.write(
                    out,
                    "Base",
                    facegenTri.vertices,
                    facegenTri.faces,
                    facegenTri.uvs
            );
        }

        Utils.print(strings.baseMeshWrote());

        for(FaceGenMorph morph : facegenTri.morphs){
            ArrayList<Vertex> morphedVertices =
                    Morph.applyMorphToVertices(
                            facegenTri.vertices,
                            morph.vertices
                    );

            File morphFile = new File(outputFolder, morph.name + ".obj");

            try (PrintWriter out = new PrintWriter(new FileWriter(morphFile))) {
                ObjWriter.write(
                        out,
                        morph.name,
                        morphedVertices,
                        facegenTri.faces
                );
            }
            Utils.print(strings.morphWrite() + morph.name);
        }
    }


    public static void extractBodyslideTriFile(File triFile, File nifFile, File outputFolder) throws IOException {
        TriData triData = TriReader.readTri(triFile);
        NifData nif = NifReader.readNif(nifFile);

        ArrayList<NifTriShape> shapes = NifReader.readTriShapes(nif);


        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        for (NifTriShape shape : shapes) {
            TriShape triShape = findTriShapeByName(triData, shape.name);

            if (triShape == null) {
                Utils.print(strings.noTriForMesh() + shape.name);
                continue;
            }

            NifSkinInstance skin = NifSkinInstance.readSkinInstance(nif, shape.skinInstanceRef);
            NiSkinPartition partition = NiSkinPartition.readSkinPartition(nif, skin.skinPartitionRef);
            ArrayList<Vertex> baseVertices = Utils.chooseExportVertices(partition);
            ArrayList<UV> baseUvs = Utils.chooseExportUvs(partition);

            // Base mesh goes directly in root folder
            File baseFile = new File(
                    outputFolder,
                    Utils.safeFileName(shape.name) + ".obj"
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
                    Utils.safeFileName(shape.name)
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
                        Utils.safeFileName(morph.name) + ".obj"
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
        Utils.print(strings.extractionComplete());
    }



    public static String stripExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');

        if (dot <= 0) {
            return fileName;
        }

        return fileName.substring(0, dot);
    }
}
