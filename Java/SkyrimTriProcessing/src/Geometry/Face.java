package Geometry;

import Readers.BlockReader;

public class Face {
    public int a;
    public int b;
    public int c;

    public Face(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }


    public Face readTriangle(BlockReader r) {
        int a = r.readU16();
        int b = r.readU16();
        int c = r.readU16();

        return new Face(a, b, c);
    }
}
