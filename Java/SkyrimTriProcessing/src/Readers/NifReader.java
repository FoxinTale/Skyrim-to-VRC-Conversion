package Readers;

import NifData.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class NifReader {

    private static NifHeader readHeader(InputStream in) throws IOException {

        NifHeader header = new NifHeader();

        header.headerLine = readAsciiLine(in).trim();

        header.versionRaw = BinaryReader.readU32(in, "version");
        header.endian = BinaryReader.readU8(in, "endian");
        header.userVersion = BinaryReader.readU32(in, "user version");

        header.numBlocks = checkedInt(
                BinaryReader.readU32(in, "block count"),
                "block count"
        );

        header.userVersion2 = BinaryReader.readU32(in, "user version 2");

        readExportInfo(in, header);
        readBlockTypeTable(in, header);
        readStringTable(in, header);

        return header;
    }

    private static void readExportInfo(InputStream in, NifHeader header) throws IOException {
        header.exportInfoCount = BinaryReader.readU16(in, "export info count");

        for (int i = 0; i < header.exportInfoCount; i++) {
            int length = BinaryReader.readU8(in, "export info " + i + " length");

            byte[] data = BinaryReader.readExact(
                    in,
                    length,
                    "export info " + i
            );

            header.exportInfo.add(new String(data, StandardCharsets.UTF_8));
        }

        header.postExportField =
                BinaryReader.readU16(in, "post-export field");
    }

    private static void readBlockTypeTable(InputStream in, NifHeader header) throws IOException {
        header.blockTypeCount =
                BinaryReader.readU16(in, "block type count");

        for (int i = 0; i < header.blockTypeCount; i++) {
            String blockType =
                    BinaryReader.readStringU32(in, "block type " + i);

            header.blockTypes.add(blockType);
        }

        for (int i = 0; i < header.numBlocks; i++) {
            int typeIndex =
                    BinaryReader.readU16(in, "block " + i + " type index");

            header.blockTypeIndices.add(typeIndex);
        }

        for (int i = 0; i < header.numBlocks; i++) {
            long size =
                    BinaryReader.readU32(in, "block " + i + " size");

            header.blockSizes.add(size);
        }
    }

    private static void readStringTable(InputStream in, NifHeader header) throws IOException {
        header.stringCount = checkedInt(
                BinaryReader.readU32(in, "string count"),
                "string count"
        );

        header.maxStringLength = checkedInt(
                BinaryReader.readU32(in, "max string length"),
                "max string length"
        );

        for (int i = 0; i < header.stringCount; i++) {
            String text =
                    BinaryReader.readStringU32(in, "string " + i);

            header.strings.add(text);
        }
    }

    private static String readAsciiLine(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') {
                break;
            }

            out.write(b);
        }

        return new String(out.toByteArray(), StandardCharsets.US_ASCII);
    }

    public static int checkedInt(long value, String label) throws IOException {
        if (value > Integer.MAX_VALUE) {
            throw new IOException(label + " too large for Java int: " + value);
        }

        return (int) value;
    }


    public static NifData readNif(File path) throws IOException {
        try (InputStream in =
                     new BufferedInputStream(
                             new FileInputStream(path))) {

            NifHeader header = readHeader(in);
            ArrayList<NifBlock> blocks = readBlocks(in, header);

            return new NifData(header, blocks);
        }
    }

    private static ArrayList<NifBlock> readBlocks(
            InputStream in,
            NifHeader header
    ) throws IOException {

        ArrayList<NifBlock> blocks = new ArrayList<>();

        for (int i = 0; i < header.numBlocks; i++) {
            int typeIndex = header.blockTypeIndices.get(i);

            if (typeIndex < 0 || typeIndex >= header.blockTypes.size()) {
                throw new IOException("Invalid block type index "
                        + typeIndex + " for block " + i);
            }

            String type = header.blockTypes.get(typeIndex);

            int size = checkedInt(
                    header.blockSizes.get(i),
                    "block " + i + " size"
            );

            byte[] data = BinaryReader.readExact(
                    in,
                    size,
                    "block " + i + " data"
            );

            blocks.add(new NifBlock(i, type, size, data));
        }

        return blocks;
    }


    public static ArrayList<NifTriShape> readTriShapes(NifData nif) {
        ArrayList<NifTriShape> shapes = new ArrayList<>();

        for (NifBlock block : nif.blocks) {
            if (!block.type.equals("BSTriShape")) {
                continue;
            }

            BlockReader r = new BlockReader(block.data);

            r.readI32(); // unknown0

            int nameIndex = r.readI32();
            String name = nif.header.strings.get(nameIndex);

            r.readI32(); // extraDataCount
            r.readI32(); // controllerRef

            r.readU16(); // flags
            r.readU16(); // unknownAfterFlags

            // translation
            r.readF32();
            r.readF32();
            r.readF32();

            // rotation matrix
            for (int i = 0; i < 9; i++) {
                r.readF32();
            }

            r.readF32(); // scale

            r.readI32(); // unknown ref at 72

            // bounds
            r.readF32();
            r.readF32();
            r.readF32();
            r.readF32();

            int skinInstanceRef = r.readI32();
            int shaderPropertyRef = r.readI32();
            int alphaPropertyRef = r.readI32();

            if (!isValidBlockRefOfType(nif, skinInstanceRef, "BSDismemberSkinInstance")) {
                skinInstanceRef = shaderPropertyRef;
                shaderPropertyRef = alphaPropertyRef;
                alphaPropertyRef = r.readI32();
            }

            shapes.add(new NifTriShape(
                    block.index,
                    name,
                    skinInstanceRef,
                    shaderPropertyRef,
                    alphaPropertyRef
            ));
        }

        return shapes;
    }


    private static boolean isValidBlockRefOfType(NifData nif, int ref, String type) {
        if (ref < 0 || ref >= nif.blocks.size()) {
            return false;
        }

        return nif.blocks.get(ref).type.equals(type);
    }


}
