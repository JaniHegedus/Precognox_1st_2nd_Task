package precognox.apptest.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

public class XmlProcessorService {

    public Map<String, Integer> processXmlFile(String filePath) {
        Map<String, Integer> nameFrequencyMap = new TreeMap<>();

        // Check if the file is GZIP or plain XML
        if (filePath.endsWith(".gz")) {
            // Handle GZIP file
            try (InputStream fileStream = new FileInputStream(filePath);
                 InputStream gzipStream = new GZIPInputStream(fileStream);
                 Reader decoder = new InputStreamReader(gzipStream, StandardCharsets.UTF_8)) {

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(decoder));

                nameFrequencyMap = parseDocument(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Handle plain XML file
            try (InputStream fileStream = new FileInputStream(filePath);
                 Reader decoder = new InputStreamReader(fileStream, StandardCharsets.UTF_8)) {

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(decoder));

                nameFrequencyMap = parseDocument(doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return nameFrequencyMap;
    }

    private Map<String, Integer> parseDocument(Document doc) {
        Map<String, Integer> nameFrequencyMap = new TreeMap<>();
        NodeList nodes = doc.getElementsByTagName("datafield");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if ("100".equals(element.getAttribute("tag"))) {
                NodeList subfields = element.getElementsByTagName("subfield");
                for (int j = 0; j < subfields.getLength(); j++) {
                    Element subfield = (Element) subfields.item(j);
                    if ("a".equals(subfield.getAttribute("code"))) {
                        String name = subfield.getTextContent();
                        nameFrequencyMap.put(name, nameFrequencyMap.getOrDefault(name, 0) + 1);
                    }
                }
            }
        }
        return nameFrequencyMap;
    }
}
