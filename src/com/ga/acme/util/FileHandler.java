package com.ga.acme.util;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(object.toString());
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }
}
