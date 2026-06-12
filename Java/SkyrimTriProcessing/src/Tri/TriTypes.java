package Tri;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TriTypes {
    public static TriType detect(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            byte[] magic = new byte[8];

            int read = in.read(magic);

            if (read < 4) {
                return TriType.UNKNOWN;
            }

            String first4 = new String(magic, 0, 4, "US-ASCII");

            if (first4.equals("PIRT")) {
                return TriType.BODYSLIDE;
            }

            if (read >= 8) {
                String first8 = new String(magic, 0, 8, "US-ASCII");

                if (first8.equals("FRTRI003")) {
                    return TriType.FACEGEN;
                }
            }
            return TriType.UNKNOWN;
        }
    }
}
