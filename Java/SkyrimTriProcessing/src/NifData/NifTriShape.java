package NifData;

public class NifTriShape {
    public int blockIndex;
    public String name;

    public int skinInstanceRef;
    public int shaderPropertyRef;
    public int alphaPropertyRef;

    public NifTriShape(int blockIndex, String name,
                       int skinInstanceRef,
                       int shaderPropertyRef,
                       int alphaPropertyRef) {
        this.blockIndex = blockIndex;
        this.name = name;
        this.skinInstanceRef = skinInstanceRef;
        this.shaderPropertyRef = shaderPropertyRef;
        this.alphaPropertyRef = alphaPropertyRef;
    }
}