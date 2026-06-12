package Core;

import Geometry.Face;
import Geometry.UV;
import Geometry.Vertex;
import NifData.NiSkinPartition;

import java.io.File;
import java.util.ArrayList;

public class Utils {

    public static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');

        if (dot <= 0) {
            return name;
        }

        return name.substring(0, dot);
    }

    public static String normalizeMeshName(String name) {
        return name
                .replaceAll("(?i)_0$", "")
                .replaceAll("(?i)_1$", "")
                .replaceAll("[_\\-\\s]", "")
                .toLowerCase();
    }



    public static ArrayList<Vertex> chooseExportVertices(NiSkinPartition partition) {
        int maxFaceIndex = getMaxFaceIndex(partition.faces);

        if (partition.objVertices != null
                && !partition.objVertices.isEmpty()
                && maxFaceIndex < partition.objVertices.size()) {
            return partition.objVertices;
        }

        return partition.vertices;
    }

    public static ArrayList<UV> chooseExportUvs(NiSkinPartition partition) {
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

    static String safeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    public static void print(String s){
        System.out.println(" " + s);
    }

    public File determineOutputFolder(
            File triFile,
            String outputText
    ) {

        if (outputText != null
                && !outputText.trim().isEmpty()) {

            return new File(outputText.trim());
        }

        String triName =
                stripExtension(triFile.getName());

        return new File(
                triFile.getParentFile(),
                triName
        );
    }
}
