package Readers;

import ESP.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

        return record;
    }

    private static ArrayList<EspSubrecord> readSubrecords(
            BinaryReader br,
            EspRecord record
    ) throws IOException {

        ArrayList<EspSubrecord> subrecords =
                new ArrayList<EspSubrecord>();

        br.seek(record.dataStart);

        while (br.position() < record.dataEnd) {
            String type = br.readAscii(4);
            int size = br.readUInt16LE();
            byte[] data = br.readBytes(size);

            subrecords.add(new EspSubrecord(type, size, data));
        }

        return subrecords;
    }
}