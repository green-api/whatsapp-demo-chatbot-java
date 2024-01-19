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

    public String getText(String string, Language language) {
        return ((Map<String, Object>) data.get(string)).get(language.getValue()).toString();
    }

    public String getText(String string) {
        return data.get(string).toString();
    }

    public String getText(String string, Language language, String string2) {
        var map1 = (Map<String, Object>) data.get(string);
        log.warn(map1);
        var map2 = (Map<String, Object>) map1.get(language.getValue());
        log.warn(map2);
        var result = map2.get(string2);
        log.warn(result);

        return result.toString();
    }

    private static Map<String, Object> loadDataFromYaml(Path path) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            Yaml yaml = new Yaml();
            return yaml.load(inputStream);

        } catch (Exception e) {
            throw new RuntimeException("Not Found strings.yml (src/main/resources/strings.yml)");
        }
    }
}
