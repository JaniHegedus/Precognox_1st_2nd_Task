package precognox.apptest;

import org.apache.commons.cli.*;
import precognox.apptest.service.XmlProcessorService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Create command-line options
        Options options = new Options();
        options.addOption("f", "file", true, "Path to the XML file");
        options.addOption("n", "name", true, "Filter names starting with (optional)");
        options.addOption("o", "order", true, "Order by frequency (asc/desc, optional)");

        CommandLineParser parser = new DefaultParser();
        try {
            // Parse command-line arguments
            CommandLine cmd = parser.parse(options, args);
            if (!cmd.hasOption("f")) {
                System.out.println("Please provide the path to the XML file using -f option.");
                System.exit(1);
            }

            String filePath = cmd.getOptionValue("f");
            String nameFilter = cmd.getOptionValue("n");
            String orderBy = cmd.getOptionValue("o");

            // Process XML file using XmlProcessorService
            XmlProcessorService processor = new XmlProcessorService();
            Map<String, Integer> nameFrequencyMap = processor.processXmlFile(filePath);

            // Filter and sort the results
            List<Map.Entry<String, Integer>> filteredResults = filterAndSortResults(nameFrequencyMap, nameFilter, orderBy);

            // Display the results
            displayResults(filteredResults);

        } catch (ParseException e) {
            System.out.println("Error parsing command-line arguments: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("XMLProcessor", options);
        }
    }

    private static List<Map.Entry<String, Integer>> filterAndSortResults(Map<String, Integer> nameFrequencyMap, String nameFilter, String orderBy) {
        List<Map.Entry<String, Integer>> filteredResults = new ArrayList<>(nameFrequencyMap.entrySet());

        if (nameFilter != null) {
            filteredResults.removeIf(entry -> !entry.getKey().startsWith(nameFilter));
        }

        if ("asc".equalsIgnoreCase(orderBy)) {
            filteredResults.sort(Map.Entry.comparingByValue());
        } else if ("desc".equalsIgnoreCase(orderBy)) {
            filteredResults.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        }

        return filteredResults;
    }

    private static void displayResults(List<Map.Entry<String, Integer>> results) {
        for (Map.Entry<String, Integer> entry : results) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}