package ESP;

import java.util.ArrayList;

public class EspRecord extends EspElement {
    public long flags;
    public long formId;
    public int timestamp;
    public int versionControlInfo;
    public int version;
    public int unknown;
    public boolean compressed;

    public ArrayList<EspSubrecord> subrecords = new ArrayList<EspSubrecord>();

    @Override
    public String toString() {
        return type +
                " formId=0x" + Long.toHexString(formId) +
                " size=" + size +
                " data=[" + dataStart + "-" + dataEnd + "]";
    }


}