package ESP;

public class EspSubrecord {
    public String type;
    public int size;
    public byte[] data;

    public EspSubrecord(String type, int size, byte[] data) {
        this.type = type;
        this.size = size;
        this.data = data;
    }

    public String asString() {
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
