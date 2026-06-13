import ESP.EspFile;
import EspRecords.*;
import Helpers.RacialAssets;
import Helpers.EspDataRecords;
import Readers.EspReader;

import java.io.File;
import java.io.IOException;

import static Helpers.RacialAssets.buildRaceAssetReport;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {

        EspFile esp = EspReader.read(new File("Crimes against Nature.esm"));
        EspDataRecords record = new EspDataRecords(esp);

        for (Race race : record.races) {
            RacialAssets report = buildRaceAssetReport(race, record);
            printRaceReport(report);
        }
    }


    private static void printRaceReport(RacialAssets report) {
        Race race = report.race;

        System.out.println("Race: " + race.name + " / " + race.editorId);

        System.out.println("NIFs:");
        for (String path : report.nifPaths) {
            System.out.println("  " + path);
        }

        System.out.println("TRIs:");
        for (String path : report.triPaths) {
            System.out.println("  " + path);
        }

        System.out.println("Textures:");
        for (String path : report.texturePaths) {
            System.out.println("  " + path);
        }

        System.out.println();
    }

}
