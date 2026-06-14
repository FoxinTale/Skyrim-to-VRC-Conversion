package Lang;

public class StringsEN implements Strings {
    /*
        When translating, please keep any spaces before, or after the words.
        We can lengthen tri to triangle, or morph, morphs, data file, whatever fits the closest for the translated language.
        If the exact word does not exist, you can use the closest approximation.
     */

    /* --------------------------------------------------------------------------------------
        Text elements for the main GUI.
    -------------------------------------------------------------------------------------*/
    public String appTitle() { return "Tri Toolkit - Version "; }
    public String selectTri() { return "Select Tri File: "; }
    public String selectNif() { return "Select Nif File: "; }
    public String selectOutput() { return "Select Output Folder: "; }
    public String modeSelect() { return "Select Mode: "; }
    public String faceGenButton() { return "FaceGen Tri File"; }
    public String bodyslideButton() { return "Bodyslide Tri File"; }
    public String openButton() {return "Open"; }
    public String run() { return "Extract"; }

    /* --------------------------------------------------------------------------------------
        Tooltips for the GUI. Found in the Core.GUI package.
    -------------------------------------------------------------------------------------*/
    public String bodyslideModeTooltip() { return "Click this button if you're extracting a BodySlide export, or a body mesh. Needs a NIF and a TRI file."; }
    public String facegenModeTooltip() { return "Click this if you're wanting to extract a facegen related tri file."; }
    public String triFileTextTooltip() { return "The path to your tri file, click the open button and navigate to it."; }
    public String nifFileTextTooltip() { return "The path to your nif file, click the open button and navigate to it."; }
    public String outputFolderTextTooltip() { return "Where the extractor puts all the files. Click the open button to set it. Or not."; }
    public String consoleWindowTooltip() { return "This is the big window where any output goes. Progress, extracted files, errors, and more!"; }

    /* --------------------------------------------------------------------------------------
        Errors for the GUI package. Well, more like telling the user they've forgotten something.
    -------------------------------------------------------------------------------------*/
    public String selectTriFileMessage() { return "Please select a TRI file."; }
    public String selectTriFileTitle() { return "Missing TRI File"; }
    public String selectNifFileMessage() { return "Please select a NIF file."; }
    public String selectNifFileTitle() { return "Missing NIF File"; }
    public String outputFolderErrorMessage() { return "Could not create the output folder:\n"; }
    public String outputFolderErrorTitle(){ return "Output Folder Error"; }
    public String triTypeMismatch01() { return "The selected TRI does not match the chosen mode.\n\n"; }
    public String triTypeMismatch02() { return "Selected mode: "; }
    public String triTypeMismatch03() { return "Detected TRI type: "; }
    public String triTypeMismatch04() { return "Please choose the correct mode or select a different TRI file."; }
    public String triTypeMismatchTitle() { return "TRI Type Mismatch"; }



    public String outputFolderNotEmpty() { return "The output folder already contains files."; }


    /* --------------------------------------------------------------------------------------
       Content and text printed during the extraction process, found in the Core.Extractor package.
    -------------------------------------------------------------------------------------*/
    public String baseMeshWrote(){return "Base mesh wrote";}
    public String morphWrite(){ return "Wrote morph: ";}
    public String noTriForMesh(){ return "No TRI shape for "; }
    public String wrote(){ return "Wrote "; }
    public String forShape(){ return " for shape "; }
    public String extracted() { return " extracted."; }
    public String extractionComplete() { return "Extraction Complete!"; }


    /* --------------------------------------------------------------------------------------
            Abandon all hope below here, as here bee where the real errors live.
     -------------------------------------------------------------------------------------*/




    /* --------------------------------------------------------------------------------------
        Errors for the FaceGen Tri Reader, found the Readers.FaceGenTriReader package.
    -------------------------------------------------------------------------------------*/
    public String unsupportedFacegenTriError(){ return "This does not appear to be a supported FaceGen TRI file.\n\n"; }
    public String labelledVertsError() { return "This FaceGen TRI file uses labelled vertices, which are not currently supported.\n"; }
    public String labelledVerts(){ return "Labelled vertices: "; }
    public String labelledPointsError() { return "This FaceGen TRI file uses labelled surface points, which are not currently supported.\n"; }
    public String labelledPoints() { return "Labelled surface points: "; }
}