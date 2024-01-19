package com.greenapi.demoChatbot.util;

import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Log4j2
public class StringsYml {

    private final Map<String, Object> data = loadDataFromYaml(Paths.get("src/main/resources/strings.yml"));
    private static final Yaml yaml = new Yaml();

    public String getText(String string, Language language) {
        return ((Map<String, Object>) data.get(string)).get(language.getValue()).toString();
    }

    public String getText(String string) {
        return data.get(string).toString();
    }

    public String getText(String string, Language language, String string2) {
        return ((Map<String, Object>)(((Map<String, Object>) data.get(string)).get(language.getValue()))).get(string2).toString();
    }

    private static Map<String, Object> loadDataFromYaml(Path path) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return yaml.load(inputStream);

        } catch (Exception e) {
            throw new RuntimeException("Not Found strings.yml (src/main/resources/strings.yml)");
        }
    }
}
