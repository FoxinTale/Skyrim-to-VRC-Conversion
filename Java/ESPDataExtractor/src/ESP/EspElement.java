package ESP;

public abstract class EspElement {
    public String type;
    public long offset;
    public long size;
    public long dataStart;
    public long dataEnd;

    public boolean isGroup() {
        return this instanceof EspGroup;
    }

    public boolean isRecord() {
        return this instanceof EspRecord;
    }
}