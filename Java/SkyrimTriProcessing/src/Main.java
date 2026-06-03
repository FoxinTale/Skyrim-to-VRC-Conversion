import Geometry.TriData;
import Geometry.Vertex;
import NifData.*;
import Readers.NifReader;
import Readers.TriReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


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

            System.out.println(shape.name);
            System.out.println("  raw vertices: " + partition.vertices.size());
            System.out.println("  obj vertices: " + partition.objVertices.size());
            System.out.println("  faces: " + partition.faces.size());

            File outFile = new File(shape.name + ".obj");

            ObjWriter.writePartition(outFile, partition, shape.name);
    }
}
}