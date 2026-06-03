import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class ReadXML {


    public static Map<String, ArrayList<Slider>> readSliderFile(File xmlFile) throws Exception {
        Map<String, ArrayList<Slider>> categories = new TreeMap<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList categoryNodes = doc.getElementsByTagName("Category");

        for (int i = 0; i < categoryNodes.getLength(); i++) {
            Element categoryElement = (Element) categoryNodes.item(i);

            String categoryName = categoryElement.getAttribute("name");

            ArrayList<Slider> sliders = new ArrayList<>();

            NodeList childNodes = categoryElement.getChildNodes();

            for (int j = 0; j < childNodes.getLength(); j++) {
                Node child = childNodes.item(j);

                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                Element element = (Element) child;

                if (!element.getTagName().equals("Slider")) {
                    continue;
                }

                String name = element.getAttribute("name");
                String displayName = element.getAttribute("displayname");

                sliders.add(new Slider(name, displayName));
            }

            categories.put(categoryName, sliders);
        }
        return categories;
    }

}
