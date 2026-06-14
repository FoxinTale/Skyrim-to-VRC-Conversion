package RaceMenu;

public class RaceMenuHeadPart {
    public long formId;
    public String formIdentifier;
    public int type;

    public String pluginName;
    public long localFormId;

    public RaceMenuHeadPart(long formId, String formIdentifier, int type) {
        this.formId = formId;
        this.formIdentifier = formIdentifier;
        this.type = type;

        parseFormIdentifier();
    }

    private void parseFormIdentifier() {
        if (formIdentifier == null) {
            return;
        }

        int pipe = formIdentifier.indexOf('|');

        if (pipe < 0) {
            return;
        }

        pluginName = formIdentifier.substring(0, pipe);

        String hex = formIdentifier.substring(pipe + 1);

        try {
            localFormId = Long.parseLong(hex, 16);
        } catch (NumberFormatException e) {
            localFormId = 0;
        }
    }
}
