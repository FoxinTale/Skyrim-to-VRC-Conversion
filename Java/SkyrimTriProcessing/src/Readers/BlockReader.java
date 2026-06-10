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

    public float readF16() {
        int h = readU16();

        int sign = (h >>> 15) & 0x00000001;
        int exp  = (h >>> 10) & 0x0000001F;
        int mant = h & 0x000003FF;

        int f;

        if (exp == 0) {
            if (mant == 0) {
                f = sign << 31;
            } else {
                while ((mant & 0x00000400) == 0) {
                    mant <<= 1;
                    exp--;
                }

                exp++;
                mant &= ~0x00000400;

                f = (sign << 31)
                        | ((exp + (127 - 15)) << 23)
                        | (mant << 13);
            }
        } else if (exp == 31) {
            f = (sign << 31)
                    | 0x7F800000
                    | (mant << 13);
        } else {
            f = (sign << 31)
                    | ((exp + (127 - 15)) << 23)
                    | (mant << 13);
        }

        return Float.intBitsToFloat(f);
    }
}