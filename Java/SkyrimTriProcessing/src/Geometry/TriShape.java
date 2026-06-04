package Geometry;

import java.util.List;

public class TriShape {
    public String name;
    public List<Morph> morphs;

    public TriShape(String name) {
        this.name = name;
    }

    public static TriShape findTriShapeByName(TriData triData, String name) {
        for (TriShape shape : triData.shapes) {
            if (shape.name.equals(name)) {
                return shape;
            }
        }

        return null;
    }
}
