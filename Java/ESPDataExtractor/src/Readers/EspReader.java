package Readers;

import ESP.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class EspReader {

    public static EspFile read(File file) throws IOException {
        BinaryReader br = new BinaryReader(file);

        try {
            EspFile esp = new EspFile();
            esp.elements.addAll(readElements(br, 0, br.length()));
            return esp;
        } finally {
            br.close();
        }
    }

    private static ArrayList<EspElement> readElements(
            BinaryReader br,
            long start,
            long end
    ) throws IOException {

        ArrayList<EspElement> elements =
                new ArrayList<EspElement>();

        br.seek(start);

        while (br.position() < end) {
            long offset = br.position();

            String type = br.readAscii(4);
            long size = br.readUInt32LE();

            if ("GRUP".equals(type)) {
                EspGroup group = readGroup(br, offset, type, size);
                group.children.addAll(readElements(br, group.dataStart, group.dataEnd));

                br.seek(offset + size);
                elements.add(group);
            } else {
                EspRecord record = readRecord(br, offset, type, size);
                record.subrecords.addAll(readSubrecords(br, record));

                br.seek(record.dataEnd);
                elements.add(record);
            }
        }

        return elements;
    }

    private static EspGroup readGroup(
            BinaryReader br,
            long offset,
            String type,
            long size
    ) throws IOException {

        EspGroup group = new EspGroup();

        group.type = type;
        group.offset = offset;
        group.size = size;

        group.labelRaw = br.readUInt32LE();
        group.label = BinaryReader.intToAscii((int) group.labelRaw);
        group.groupType = br.readInt32LE();

        group.timestamp = br.readUInt16LE();
        group.versionControlInfo = br.readUInt16LE();
        group.version = br.readUInt16LE();
        group.unknown = br.readUInt16LE();

        group.dataStart = br.position();
        group.dataEnd = offset + size;

        return group;
    }

    private static EspRecord readRecord(
            BinaryReader br,
            long offset,
            String type,
            long size
    ) throws IOException {

        EspRecord record = new EspRecord();

        record.type = type;
        record.offset = offset;
        record.size = size;

        record.flags = br.readUInt32LE();
        record.formId = br.readUInt32LE();

        record.timestamp = br.readUInt16LE();
        record.versionControlInfo = br.readUInt16LE();
        record.version = br.readUInt16LE();
        record.unknown = br.readUInt16LE();

        record.dataStart = br.position();
        record.dataEnd = record.dataStart + size;
        record.compressed = (record.flags & 0x00040000L) != 0;

        return record;
    }

    private static ArrayList<EspSubrecord> readSubrecords(
            BinaryReader br,
            EspRecord record
    ) throws IOException {
        if (record.compressed) {
            return readCompressedSubrecords(br, record);
        }

        ArrayList<EspSubrecord> subrecords =
                new ArrayList<EspSubrecord>();

        br.seek(record.dataStart);

        int extendedSize = -1;

        while (br.position() < record.dataEnd) {
            long subStart = br.position();

            String type = br.readAscii(4);
            int size = br.readUInt16LE();

            if ("XXXX".equals(type)) {
                byte[] sizeData = br.readBytes(size);

                if (sizeData.length >= 4) {
                    extendedSize = (int) readUInt32LE(sizeData, 0);
                }

                continue;
            }

            int actualSize = size;

            if (extendedSize >= 0) {
                actualSize = extendedSize;
                extendedSize = -1;
            }

            if (br.position() + actualSize > record.dataEnd) {
                throw new IOException(
                        "Subrecord overruns record: " +
                                record.type +
                                " formId=0x" + Long.toHexString(record.formId) +
                                " sub=" + type +
                                " at=" + subStart +
                                " size=" + actualSize +
                                " recordEnd=" + record.dataEnd
                );
            }

            byte[] data = br.readBytes(actualSize);

            subrecords.add(new EspSubrecord(type, actualSize, data));
        }

        return subrecords;
    }

    private static ArrayList<EspSubrecord> readCompressedSubrecords(
            BinaryReader br,
            EspRecord record
    ) throws IOException {

        br.seek(record.dataStart);

        long uncompressedSize = br.readUInt32LE();

        byte[] compressedData =
                br.readBytes((int)(record.size - 4));

        byte[] data = decompressZlib(compressedData, (int) uncompressedSize);

        return readSubrecordsFromBytes(data);
    }


    private static long readUInt32LE(byte[] data, int offset) {
        return ((long) data[offset] & 0xFF)
                | (((long) data[offset + 1] & 0xFF) << 8)
                | (((long) data[offset + 2] & 0xFF) << 16)
                | (((long) data[offset + 3] & 0xFF) << 24);
    }

    private static byte[] decompressZlib(byte[] compressedData, int expectedSize)
            throws IOException {

        Inflater inflater = new java.util.zip.Inflater();

        inflater.setInput(compressedData);

        byte[] output = new byte[expectedSize];

        try {
            int resultLength = inflater.inflate(output);

            if (resultLength != expectedSize) {
                throw new IOException(
                        "Decompressed size mismatch. Expected " +
                                expectedSize + " got " + resultLength
                );
            }

            return output;

        } catch (java.util.zip.DataFormatException e) {
            throw new IOException("Invalid compressed record data", e);
        } finally {
            inflater.end();
        }
    }


    private static ArrayList<EspSubrecord> readSubrecordsFromBytes(byte[] data)
            throws IOException {

        ArrayList<EspSubrecord> subrecords =
                new ArrayList<EspSubrecord>();

        int pos = 0;
        int extendedSize = -1;

        while (pos < data.length) {
            if (pos + 6 > data.length) {
                break;
            }

            String type = new String(data, pos, 4, "US-ASCII");
            pos += 4;

            int size =
                    (data[pos] & 0xFF)
                            | ((data[pos + 1] & 0xFF) << 8);
            pos += 2;

            if ("XXXX".equals(type)) {
                if (pos + size > data.length) {
                    break;
                }

                if (size >= 4) {
                    extendedSize =
                            ((data[pos] & 0xFF))
                                    | ((data[pos + 1] & 0xFF) << 8)
                                    | ((data[pos + 2] & 0xFF) << 16)
                                    | ((data[pos + 3] & 0xFF) << 24);
                }

                pos += size;
                continue;
            }

            int actualSize = size;

            if (extendedSize >= 0) {
                actualSize = extendedSize;
                extendedSize = -1;
            }

            if (pos + actualSize > data.length) {
                throw new IOException(
                        "Subrecord overruns decompressed record: sub=" +
                                type + " size=" + actualSize
                );
            }

            byte[] subData = new byte[actualSize];
            System.arraycopy(data, pos, subData, 0, actualSize);
            pos += actualSize;

            subrecords.add(new EspSubrecord(type, actualSize, subData));
        }

        return subrecords;
    }
}