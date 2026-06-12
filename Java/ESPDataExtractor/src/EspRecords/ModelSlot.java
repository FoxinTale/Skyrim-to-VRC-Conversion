package EspRecords;

import java.util.ArrayList;

public class ModelSlot {
    public String label;
    public String modelPath;

    public ArrayList<AlternateTexture> alternateTextures = new ArrayList<>();

    public ModelSlot(String label) {
        this.label = label;
    }

    public boolean hasData() {
        return modelPath != null && modelPath.length() > 0
                || alternateTextures.size() > 0;
    }

}