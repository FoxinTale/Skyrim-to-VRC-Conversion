package Lang;

public interface Strings {
    // Main GUI. See StringsEN or the blank file for a complete, organised breakdown of what this all is.
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
    String extracted();
    String extractionComplete();


    // Errors

    // Errors thrown by the FaceGen Tri Reader.
    String unsupportedFacegenTriError();
    String labelledVertsError();
    String labelledVerts();
    String labelledPointsError();
    String labelledPoints();
}