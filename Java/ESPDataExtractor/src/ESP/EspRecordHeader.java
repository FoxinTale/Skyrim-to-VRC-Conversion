package ESP;

public class EspRecordHeader {
    public String type;
    public long size;
    public long flags;
    public long formId;
    public long timestamp;
    public long versionControlInfo;
    public int version;
    public int unknown;

    public long dataStart;
    public long dataEnd;

    public String groupLabel;
    public int groupType;
    public long groupLabelRaw;

    public boolean isGroup() {
        return "GRUP".equals(type);
    }



    @Override
    public String toString() {
        if (isGroup()) {
            return type +
                    " size=" + size +
                    " label=" + groupLabel +
                    " groupType=" + groupType +
                    " data=[" + dataStart + "-" + dataEnd + "]";
        }

        return type +
                " size=" + size +
                " flags=0x" + Long.toHexString(flags) +
                " formId=0x" + Long.toHexString(formId) +
                " data=[" + dataStart + "-" + dataEnd + "]";
    }
}