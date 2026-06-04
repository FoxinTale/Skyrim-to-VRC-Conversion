import Geometry.Morph;
import Geometry.TriData;
import Geometry.TriShape;
import Geometry.Vertex;
import NifData.*;
import Readers.NifReader;
import Readers.TriReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static Geometry.TriShape.findTriShapeByName;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {

        File triFile = new File("torso.tri");
        File nifFile = new File("torso_1.nif");

        TriData triData = TriReader.readTri(triFile);
        NifData nif = NifReader.readNif(nifFile);

        ArrayList<NifTriShape> shapes = NifReader.readTriShapes(nif);


        for (NifTriShape shape : shapes) {
            NifSkinInstance skin =
                    NifSkinInstance.readSkinInstance(nif, shape.skinInstanceRef);

            NiSkinPartition partition =
                    NiSkinPartition.readSkinPartition(nif, skin.skinPartitionRef);

            TriShape triShape = findTriShapeByName(triData, shape.name);

            System.out.println(shape.name);
            if (triShape == null) {
                System.out.println("No TRI shape for " + shape.name);
                continue;
            }
            if (triShape.morphs.isEmpty()) {
                continue;
            }

            Morph morph = triShape.morphs.get(0);

            ArrayList<Vertex> baseVerts =
                    !partition.objVertices.isEmpty()
                            ? partition.objVertices
                            : partition.vertices;

            ArrayList<Vertex> morphedVertices =
                    Morph.applyMorphToVertices(baseVerts, morph);

            File outFile = new File(
                    shape.name
                            + "_"
                            + morph.name
                            + ".obj"
            );

            try (PrintWriter out = new PrintWriter(new FileWriter(outFile))) {
                ObjWriter.write(
                        out,
                        shape.name + "_" + morph.name,
                        morphedVertices,
                        partition.faces
                );
            }



//            System.out.println("  raw vertices: " + partition.vertices.size());
//            System.out.println("  obj vertices: " + partition.objVertices.size());
//            System.out.println("  faces: " + partition.faces.size());


    }
}
}