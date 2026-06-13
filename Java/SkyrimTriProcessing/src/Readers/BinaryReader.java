package Readers;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class BinaryReader {
    public static byte[] readExact(InputStream in, int length, String label) throws IOException {
        byte[] data = new byte[length];
        int offset = 0;

        while (offset < length) {
            int read = in.read(data, offset, length - offset);

            if (read == -1) {
                throw new EOFException("Unexpected EOF while reading " + label);
            }
            offset += read;
        }

        return data;
    }

    public static int readU8(InputStream in, String label) throws IOException {
        byte[] b = readExact(in, 1, label);

        return b[0] & 0xFF;
    }

    public static int readU16(InputStream in, String label) throws IOException {
        byte[] b = readExact(in, 2, label);

        return (b[0] & 0xFF)
                | ((b[1] & 0xFF) << 8);
    }


    public static String readStringU8(InputStream in, String label) throws IOException {
        int length = readU8(in, label + "_length");

        byte[] data = readExact(in, length, label);

        return new String(data, StandardCharsets.UTF_8);
    }


    public static float readF32(InputStream in, String label) throws IOException {
        byte[] b = readExact(in, 4, label);

        int bits = (b[0] & 0xFF)
                | ((b[1] & 0xFF) << 8)
                | ((b[2] & 0xFF) << 16)
                | ((b[3] & 0xFF) << 24);

        return Float.intBitsToFloat(bits);
    }

    public static short readI16(InputStream in, String label) throws IOException {
        byte[] b = readExact(in, 2, label);

        int value = (b[0] & 0xFF)
                | ((b[1] & 0xFF) << 8);

        return (short) value;
    }


    public static long readU32(InputStream in, String label) throws IOException {
        byte[] b = readExact(in, 4, label);

        return ((long) b[0] & 0xFF)
                | (((long) b[1] & 0xFF) << 8)
                | (((long) b[2] & 0xFF) << 16)
                | (((long) b[3] & 0xFF) << 24);
    }


    public static String readStringU32(InputStream in, String label) throws IOException {
        long lengthLong = readU32(in, label + "_length");

        if (lengthLong > Integer.MAX_VALUE) {
            throw new IOException("String too large: " + lengthLong);
        }

        int length = (int) lengthLong;

        byte[] data = readExact(in, length, label);

        return new String(data, StandardCharsets.UTF_8);
    }



}
