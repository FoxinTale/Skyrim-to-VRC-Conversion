package Lang;

public interface Strings {
    // Main GUI.
    String appTitle();
    String selectTri();
    String selectNif();
    String selectOutput();
    String modeSelect();
    String faceGenButton();
    String bodyslideButton();
    String openButton();
    String run();

    String bodyslideModeTooltip();
    String facegenModeTooltip();
    String triFileTextTooltip();
    String nifFileTextTooltip();
    String outputFolderTextTooltip();
    String scrollWindowTooltip();


    String outputFolderNotEmpty();

    // Outputs during extraction.
    String baseMeshWrote();
    String morphWrite();
    String noTriForMesh();
    String wrote();
    String forShape();
    String extractionComplete();
}