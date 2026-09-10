package com.ga.acme.util;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandler {

    public static HashMap<String, List<String>> getDataFromFile(String fileName) {
        HashMap<String, List<String>> result = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitData = line.split(",");
                List<String> values = Arrays.asList(splitData);
                result.put(splitData[0], values.subList(1, values.size()));
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }
        return result;
    }

    public static void writeToFile(String fileName, Object object) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(object.toString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }

    public static void updateLineInFile(String fileName, String id, String newContent) {
        HashMap<String, List<String>> result = getDataFromFile(fileName);
        if (result.containsKey(id)) {
            String[] splitValues = newContent.split(",");
            result.replace(id, Arrays.stream(splitValues).toList().subList(1, splitValues.length));

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (Map.Entry<String, List<String>> entry : result.entrySet()) {
                    writer.write(entry.getKey() + "," + String.join(",", entry.getValue()));
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error updating file");
            }
        }

    }
}