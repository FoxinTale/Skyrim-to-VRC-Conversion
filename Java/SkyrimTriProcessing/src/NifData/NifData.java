package NifData;

import java.util.ArrayList;

public class NifData {
    public NifHeader header;
    public ArrayList<NifBlock> blocks;

    public NifData(NifHeader header, ArrayList<NifBlock> blocks) {
        this.header = header;
        this.blocks = blocks;
    }
}