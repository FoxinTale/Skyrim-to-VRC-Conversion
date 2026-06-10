package NifData;

import Geometry.Face;
import Geometry.UV;
import Geometry.Vertex;
import Readers.BlockReader;

import java.io.IOException;
import java.util.ArrayList;

import static Geometry.UV.looksLikeUv;


public class NiSkinPartition {
    public int blockIndex;

    public float unknownFloat0;
    public int numPartitions;

    public int vertexDataSize;
    public int vertexStride;
    public int vertexCount;

    public int vertexBufferStart;

    public ArrayList<Vertex> vertices;
    public ArrayList<Face> faces = new ArrayList<>();
    public ArrayList<Integer> bones = new ArrayList<>();
    public ArrayList<Integer> vertexMap = new ArrayList<>();
    public ArrayList<Integer> stripLengths;
    public ArrayList<Vertex> objVertices;   // render/export vertex order
    public ArrayList<UV> uvs = new ArrayList<>();
    public ArrayList<UV> objUvs = new ArrayList<>();

    public int numVertices;
    public int numTriangles;
    public int numBones;
    public int numStrips;
    public int numWeightsPerVertex;
    public int vertexBufferEnd;
    public int lodLevel;

    public boolean hasVertexMap;
    public boolean hasVertexWeights;
    public boolean hasFaces;
    public boolean hasBoneIndices;

    public boolean globalVb;


    public NiSkinPartition(int blockIndex, float unknownFloat0, int numPartitions, int vertexDataSize, int vertexStride, int vertexCount, int vertexBufferStart, ArrayList<Vertex> vertices) {
        this.blockIndex = blockIndex;
        this.unknownFloat0 = unknownFloat0;
        this.numPartitions = numPartitions;
        this.vertexDataSize = vertexDataSize;
        this.vertexStride = vertexStride;
        this.vertexCount = vertexCount;
        this.vertexBufferStart = vertexBufferStart;
        this.vertices = vertices;
    }

    public NiSkinPartition() {

    }


    public static NiSkinPartition readSkinPartition(
            NifData nif,
            int blockIndex
    ) throws IOException {

        NifBlock block = nif.blocks.get(blockIndex);

        if (!block.type.equals("NiSkinPartition")) {
            throw new IOException("Block " + blockIndex
                    + " is not NiSkinPartition. It is " + block.type);
        }

        BlockReader r = new BlockReader(block.data);

        float unknownFloat0 = r.readF32();

        int numPartitions = r.readI32();
        int vertexDataSize = r.readI32();
        int vertexStride = r.readI32();

        r.skip(8); // vertex desc

        int vertexBufferStart = r.position(); // should be 20
        int vertexCount = vertexDataSize / vertexStride;
        int vertexBufferEnd = vertexBufferStart + vertexDataSize;

//        BlockReader.debugVertexRecordLayout(block, vertexBufferStart, vertexStride);

        ArrayList<Vertex> vertices = new ArrayList<>();


        // UV handling section.

        ArrayList<UV> uvs = new ArrayList<>();
        int uvOffset = 0x10;



        for (int i = 0; i < vertexCount; i++) {
            BlockReader vr = new BlockReader(block.data);
            vr.skip(vertexBufferStart + i * vertexStride);

            vertices.add(new Vertex(
                    vr.readF32(),
                    vr.readF32(),
                    vr.readF32()
            ));

            if (uvOffset >= 0) {
                BlockReader ur = new BlockReader(block.data);
                ur.skip(vertexBufferStart + i * vertexStride + uvOffset);

                float u = ur.readF16();
                float v = ur.readF16();

/*                if (!looksLikeUv(u, v)) {
                    u = 0.0f;
                    v = 0.0f;
                }*/

    //            uvs.add(new UV(u, v));
                uvs.add(new UV(u, 1.0f - v));
            } else {
    //            uvs.add(new UV(0.0f, 0.0f));
                System.out.println(uvOffset);
            }
        }


        BlockReader post = new BlockReader(block.data);
        post.skip(vertexBufferEnd);

        ArrayList<Vertex> objVertices = new ArrayList<>();
        ArrayList<UV> objUvs = new ArrayList<>();

        ArrayList<Face> faces = new ArrayList<>();
        ArrayList<Integer> bones = new ArrayList<>();
        ArrayList<Integer> vertexMap = new ArrayList<>();

        for (int p = 0; p < numPartitions; p++) {


            int numVertices = post.readU16();
            int numTriangles = post.readU16();
            int numBones = post.readU16();
            int numStrips = post.readU16();
            int numWeightsPerVertex = post.readU16();


            if (numVertices <= 0 || numVertices > vertexCount ||
                    numTriangles < 0 ||
                    numBones < 0 || numBones > 200 ||
                    numStrips < 0 || numStrips > 20 ||
                    numWeightsPerVertex < 0 || numWeightsPerVertex > 4) {

                throw new IOException(
                        "Bad partition header at partition " + p
                                + " nv=" + numVertices
                                + " nt=" + numTriangles
                                + " nb=" + numBones
                                + " ns=" + numStrips
                                + " nw=" + numWeightsPerVertex
                );
            }

            for (int i = 0; i < numBones; i++) {
                bones.add(post.readU16());
            }

            boolean hasVertexMap = post.readBoolU8();

            ArrayList<Integer> partitionVertexMap = new ArrayList<>();

            if (hasVertexMap) {
                for (int i = 0; i < numVertices; i++) {
                    int globalIndex = post.readU16();

                    partitionVertexMap.add(globalIndex);
                    vertexMap.add(globalIndex);
                }
            }

            boolean hasVertexWeights = post.readBoolU8();

            if (hasVertexWeights) {
                post.skip(numVertices * numWeightsPerVertex * 4);
            }

            ArrayList<Integer> stripLengths = new ArrayList<>();

            for (int i = 0; i < numStrips; i++) {
                stripLengths.add(post.readU16());
            }

            boolean hasFaces = post.readBoolU8();

            ArrayList<Face> partitionFaces = new ArrayList<>();

            if (hasFaces) {
                if (numStrips > 0) {
                    for (int stripLen : stripLengths) {
                        post.skip(stripLen * 2);
                    }
                } else {
                    for (int i = 0; i < numTriangles; i++) {
                        partitionFaces.add(readTriangle(post));
                    }
                }
            }

            boolean hasBoneIndices = post.readBoolU8();

            if (hasBoneIndices) {
                post.skip(numVertices * 4);
            }

            int lodLevel = post.readU8();
            boolean globalVb = post.readBoolU8();

            post.skip(8); // partition vertex descriptor

            int bytesLeft = block.data.length - post.position();
            int maxTrueTriangles = bytesLeft / 6;

            int trueTrianglesToRead = Math.min(numTriangles, maxTrueTriangles);

            for (int i = 0; i < trueTrianglesToRead; i++) {
                readTriangle(post);
            }

            int leftover = block.data.length - post.position();

            if (leftover == 2) {
                post.readU16();
            }



            int renderStart = objVertices.size();

            if (!partitionVertexMap.isEmpty()) {
                for (int globalIndex : partitionVertexMap) {
                    objVertices.add(vertices.get(globalIndex));
                    objUvs.add(uvs.get(globalIndex));
                }
            } else {
                for (int i = 0; i < numVertices; i++) {
                    objVertices.add(vertices.get(i));
                    objUvs.add(uvs.get(i));
                }
            }

            for (Face f : partitionFaces) {
                faces.add(new Face(
                        renderStart + f.a,
                        renderStart + f.b,
                        renderStart + f.c
                ));
            }

        }

        NiSkinPartition partition = new NiSkinPartition();

        partition.blockIndex = blockIndex;

        partition.unknownFloat0 = unknownFloat0;
        partition.numPartitions = numPartitions;

        partition.vertexDataSize = vertexDataSize;
        partition.vertexStride = vertexStride;
        partition.vertexCount = vertexCount;
        partition.vertexBufferStart = vertexBufferStart;
        partition.vertexBufferEnd = vertexBufferEnd;

        partition.vertices = vertices;       // raw/shared vertex buffer
        partition.objVertices = objVertices; // export/render vertex order
        partition.uvs = uvs;
        partition.objUvs = objUvs;
        partition.faces = faces;
        partition.bones = bones;
        partition.vertexMap = vertexMap;

        return partition;
    }



    private static Face readTriangle(BlockReader r) {
        return new Face(
                r.readU16(),
                r.readU16(),
                r.readU16()
        );
    }
}
