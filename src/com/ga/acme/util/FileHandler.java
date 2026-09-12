package com.ga.acme.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandler {

    public static HashMap<String, Map<String, String>> getDataFromFile(String fileName) {
        HashMap<String, Map<String, String>> result = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitData = line.split(";");
                Map<String, String> map = new HashMap<>();
                String id = null;
                for (String values : splitData) {
                    String[] value = values.split("=");
                    if (value[0].equals("id")) {
                        id = value[1];
                    }
                    if (value.length >= 2) {
                        map.put(value[0], value[1]);
                    }
                }
                if (id != null) {
                    result.put(id, map);
                }
            }
        } catch (
                IOException e) {
            System.out.println("File not found");
        }
        return result;
    }

    public static void writeToFile(String fileName, Object object) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.newLine();
            writer.write(object.toString());
        } catch (
                IOException e) {
            System.out.println("Error writing to file");
        }
    }

    public static void updateLineInFile(String fileName, String id, String newContent) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("id=" + id + ";")) {
                    lines.add(newContent);
                } else {
                    lines.add(line);
                }
            }
        } catch (
                IOException e) {
            System.out.println("File not found");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (
                IOException e) {
            System.out.println("Error updating file");
        }
    }
}