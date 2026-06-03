package Readers;

public class BlockReader {
    private byte[] data;
    private int pos = 0;

    public BlockReader(byte[] data) {
        this.data = data;
    }

    public int readI32() {
        int value =
                (data[pos] & 0xFF)
                        | ((data[pos + 1] & 0xFF) << 8)
                        | ((data[pos + 2] & 0xFF) << 16)
                        | ((data[pos + 3] & 0xFF) << 24);

        pos += 4;
        return value;
    }

    public int readU16() {
        int value =
                (data[pos] & 0xFF)
                        | ((data[pos + 1] & 0xFF) << 8);

        pos += 2;
        return value;
    }

    public int position() {
        return pos;
    }

    public int readU8() {
        int value = data[pos] & 0xFF;
        pos += 1;
        return value;
    }

    public float readF32() {
        int bits = readI32();
        return Float.intBitsToFloat(bits);
    }

    public void skip(int count) {
        if (pos + count > data.length) {
            throw new IndexOutOfBoundsException(
                    "Skip past end: pos=" + pos
                            + " count=" + count
                            + " length=" + data.length
            );
        }

        pos += count;
    }

    public boolean readBoolU8() {
        return readU8() != 0;
    }

    public long readU32() {
        long value =
                ((long) data[pos] & 0xFF)
                        | (((long) data[pos + 1] & 0xFF) << 8)
                        | (((long) data[pos + 2] & 0xFF) << 16)
                        | (((long) data[pos + 3] & 0xFF) << 24);

        pos += 4;
        return value;
    }
}