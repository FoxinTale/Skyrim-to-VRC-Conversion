package Tri;

import Geometry.Morph;

import java.util.List;

public class TriShape {
    public String name;
    public List<Morph> morphs;

    public TriShape(String name) {
        this.name = name;
    }


    public static TriShape findTriShapeByName(
            TriData triData,
            String shapeName
    ) {
        for (TriShape triShape : triData.shapes) {
            if (triShape.name.equals(shapeName)) {
                return triShape;
            }
        }

        return null;
    }
}
