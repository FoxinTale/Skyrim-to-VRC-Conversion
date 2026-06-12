package ESP;

import java.util.ArrayList;

public class EspGroup extends EspElement {
    public long labelRaw;
    public String label;
    public int groupType;

    public int timestamp;
    public int versionControlInfo;
    public int version;
    public int unknown;

    public ArrayList<EspElement> children = new ArrayList<EspElement>();

    @Override
    public String toString() {
        return "GRUP label=" + label +
                " groupType=" + groupType +
                " size=" + size +
                " data=[" + dataStart + "-" + dataEnd + "]";
    }
}