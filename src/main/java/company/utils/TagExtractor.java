package company.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TagExtractor {

    public static void main(String[] args) {
        Set<String> groups = new HashSet<>();

        // Base path - path where the test files are located
        String path = "src/test/java/company";

        // Recursively scan the directory for .java files and extract test groups
        scanDirectory(new File(path), groups);

        // Save the extracted groups to a file
        saveGroupsToFile(groups);
    }

    /**
     * Recursively scans the provided directory for .java files and processes them
     * to extract test groups annotated with @Test(groups = {...}).
     *
     * @param directory The root directory to start scanning from.
     * @param groups    The set to store extracted groups (ensures uniqueness).
     */
    private static void scanDirectory(File directory, Set<String> groups) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Recursive call for subdirectories
                        scanDirectory(file, groups);
                    } else if (file.getName().endsWith(".java")) {
                        // Process .java file to extract groups
                        extractGroupsFromFile(file, groups);
                    }
                }
            }
        }
    }

    /**
     * Reads a .java file and extracts test groups from methods annotated with @Test(groups = {...}).
     *
     * @param file   The .java file to process.
     * @param groups The set to store extracted groups (ensures uniqueness).
     */
    private static void extractGroupsFromFile(File file, Set<String> groups) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("@Test") && line.contains("groups")) {
                    // Find the start and end indices of the group values
                    int start = line.indexOf("{") + 1;
                    int end = line.indexOf("}");
                    if (start > 0 && end > start) {
                        String[] extractedGroups = line.substring(start, end).replace("\"", "").split(",");
                        for (String group : extractedGroups) {
                            groups.add(group.trim());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file.getPath(), e);
        }
    }

    /**
     * Saves the extracted groups to a Markdown file in a readable format.
     *
     * @param groups The set of extracted test groups to be saved.
     */
    private static void saveGroupsToFile(Set<String> groups) {
        File file = new File("test-groups.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("## Test Groups\n\n");
            for (String group : groups) {
                writer.write("- " + group + "\n");
            }
            //System.out.println("Test groups successfully saved to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            //throw new RuntimeException("Failed to save groups to file", e);
        }
    }
}
