package Geometry;

import java.util.List;

public class TriData {
    public List<TriShape> shapes;
    public boolean hasUvSection;

    public TriData(List<TriShape> shapes, boolean hasUvSection) {
        this.shapes = shapes;
        this.hasUvSection = hasUvSection;
    }
}