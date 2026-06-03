package NifData;

import Readers.BlockReader;

import java.io.IOException;

public class NifSkinInstance {
    public int blockIndex;
    public int skinDataRef;
    public int skinPartitionRef;

    public NifSkinInstance(int blockIndex, int skinDataRef, int skinPartitionRef) {
        this.blockIndex = blockIndex;
        this.skinDataRef = skinDataRef;
        this.skinPartitionRef = skinPartitionRef;
    }

    public static NifSkinInstance readSkinInstance(NifData nif, int blockIndex) throws IOException {
        NifBlock block = nif.blocks.get(blockIndex);

        if (!block.type.equals("BSDismemberSkinInstance")) {
            throw new IOException("Block " + blockIndex
                    + " is not BSDismemberSkinInstance. It is " + block.type);
        }

        BlockReader r = new BlockReader(block.data);

        r.readI32(); // unknown / inherited

        int skinDataRef = r.readI32();
        int skinPartitionRef = r.readI32();

        return new NifSkinInstance(
                blockIndex,
                skinDataRef,
                skinPartitionRef
        );
    }
}