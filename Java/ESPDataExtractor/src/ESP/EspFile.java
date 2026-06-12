package ESP;

import java.util.ArrayList;

public class EspFile {
    public ArrayList<EspElement> elements = new ArrayList<EspElement>();

    public void dumpTree() {
        for (EspElement element : elements) {
            dumpElement(element, 0);
        }
    }

    private void dumpElement(EspElement element, int depth) {
        String indent = "";

        for (int i = 0; i < depth; i++) {
            indent += "  ";
        }

        System.out.println(indent + element.offset + ": " + element);

        if (element instanceof EspGroup) {
            EspGroup group = (EspGroup) element;

            for (EspElement child : group.children) {
                dumpElement(child, depth + 1);
            }
        }
    }
    public java.util.ArrayList<EspRecord> getRecordsByType(String type) {
        java.util.ArrayList<EspRecord> result =
                new java.util.ArrayList<EspRecord>();

        for (EspElement element : elements) {
            collectRecordsByType(element, type, result);
        }

        return result;
    }

    private void collectRecordsByType(
            EspElement element,
            String type,
            java.util.ArrayList<EspRecord> result
    ) {
        if (element instanceof EspRecord) {
            EspRecord record = (EspRecord) element;

            if (type.equals(record.type)) {
                result.add(record);
            }
        } else if (element instanceof EspGroup) {
            EspGroup group = (EspGroup) element;

            for (EspElement child : group.children) {
                collectRecordsByType(child, type, result);
            }
        }
    }

}
