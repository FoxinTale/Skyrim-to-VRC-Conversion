package NifData;

import java.util.ArrayList;
import java.util.List;

public class NifHeader {
    public String headerLine;
    public long versionRaw;
    public int endian;
    public long userVersion;
    public int numBlocks;
    public long userVersion2;

    public int exportInfoCount;
    public List<String> exportInfo = new ArrayList<>();

    public int postExportField;
    public int blockTypeCount;

    public List<String> blockTypes = new ArrayList<>();

    public List<Integer> blockTypeIndices = new ArrayList<>();

    public List<Long> blockSizes = new ArrayList<>();

    public int stringCount;
    public int maxStringLength;
    public List<String> strings = new ArrayList<>();

    public int groupCount;
    public ArrayList<Long> groups = new ArrayList<>();
}