package NifData;

public class NifBlock {
    public int index;
    public String type;
    public long size;
    public byte[] data;

    public NifBlock(int index, String type, long size, byte[] data) {
        this.index = index;
        this.type = type;
        this.size = size;
        this.data = data;
    }

}