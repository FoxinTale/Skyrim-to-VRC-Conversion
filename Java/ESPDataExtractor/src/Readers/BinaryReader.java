package Readers;

import java.io.*;

public class BinaryReader {
    private final RandomAccessFile raf;

    public BinaryReader(File file) throws FileNotFoundException {
        this.raf = new RandomAccessFile(file, "r");
    }

    public long position() throws IOException {
        return raf.getFilePointer();
    }

    public long length() throws IOException {
        return raf.length();
    }

    public void seek(long pos) throws IOException {
        raf.seek(pos);
    }

    public byte[] readBytes(int count) throws IOException {
        byte[] data = new byte[count];
        raf.readFully(data);
        return data;
    }

    public String readAscii(int count) throws IOException {
        return new String(readBytes(count), "US-ASCII");
    }

    public int readUInt16LE() throws IOException {
        int b0 = raf.readUnsignedByte();
        int b1 = raf.readUnsignedByte();
        return b0 | (b1 << 8);
    }

    public int readInt32LE() throws IOException {
        int b0 = raf.readUnsignedByte();
        int b1 = raf.readUnsignedByte();
        int b2 = raf.readUnsignedByte();
        int b3 = raf.readUnsignedByte();

        return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
    }

    public long readUInt32LE() throws IOException {
        return readInt32LE() & 0xFFFFFFFFL;
    }

    public void close() throws IOException {
        raf.close();
    }

    static String intToAscii(int value) {
        char c0 = (char) (value & 0xFF);
        char c1 = (char) ((value >> 8) & 0xFF);
        char c2 = (char) ((value >> 16) & 0xFF);
        char c3 = (char) ((value >> 24) & 0xFF);

        return "" + c0 + c1 + c2 + c3;
    }

    static String bytesToCString(byte[] data) {
        int len = 0;

        while (len < data.length && data[len] != 0) {
            len++;
        }

        try {
            return new String(data, 0, len, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
}