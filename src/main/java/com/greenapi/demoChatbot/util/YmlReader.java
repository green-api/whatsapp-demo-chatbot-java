package com.greenapi.demoChatbot.util;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class YmlReader {
    public static String getString(String[] keys) {
        Map<String, Object> strings;
        try {
            strings = getStrings();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        for (String key : keys) {
            if (strings.containsKey(key)) {
                var value = strings.get(key);
                if (value instanceof String) {
                    return (String) value;
                } else if (value instanceof Map) {
                    strings = (Map<String, Object>) value;
                }
            } else {
                return "";
            }
        }

        return "";
    }

    private static Map<String, Object> getStrings() throws IOException {
        var filePath = Paths.get("src/main/resources/strings.yml");
        var fileContent = Files.readAllBytes(filePath);

        var yaml = new Yaml();

        return yaml.load(new String(fileContent));
    }
}
