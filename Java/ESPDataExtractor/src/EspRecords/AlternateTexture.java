package EspRecords;

import java.util.ArrayList;

public class AlternateTexture {
    public String name3d;
    public long textureSetFormId;
    public long index;

    public AlternateTexture(String name3d, long textureSetFormId, long index) {
        this.name3d = name3d;
        this.textureSetFormId = textureSetFormId;
        this.index = index;
    }


    public static ArrayList<AlternateTexture> parseAlternateTextures(byte[] data) {
        ArrayList<AlternateTexture> result = new ArrayList<>();

        if (data.length < 4) {
            return result;
        }

        int pos = 0;

        long count = readUInt32LE(data, pos);
        pos += 4;

        for (int entry = 0; entry < count; entry++) {
            if (pos + 4 > data.length) {
                break;
            }

            int nameLength = (int) readUInt32LE(data, pos);
            pos += 4;

            if (nameLength < 0 || pos + nameLength > data.length) {
                break;
            }

            String name3d;

            int stringLength = nameLength;

            if (stringLength > 0 && data[pos + stringLength - 1] == 0) {
                stringLength--;
            }

            try {
                name3d = new String(data, pos, stringLength, "UTF-8");
            } catch (Exception e) {
                name3d = "";
            }

            pos += nameLength;

            if (pos + 8 > data.length) {
                break;
            }

            long textureSetFormId = readUInt32LE(data, pos);
            pos += 4;

            long index = readUInt32LE(data, pos);
            pos += 4;

            result.add(new AlternateTexture(name3d, textureSetFormId, index));
        }

        return result;
    }

    private static long readUInt32LE(byte[] data, int offset) {
        return ((long) data[offset] & 0xFF)
                | (((long) data[offset + 1] & 0xFF) << 8)
                | (((long) data[offset + 2] & 0xFF) << 16)
                | (((long) data[offset + 3] & 0xFF) << 24);
    }
}