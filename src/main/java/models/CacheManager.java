package models;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CacheManager {
    private String cacheFilePath;
    private Map<String, String> classLabelsByWords;

    public CacheManager(String cacheFilePath) {
        this.cacheFilePath = cacheFilePath;
        classLabelsByWords = new HashMap<>();

        loadCache();
    }

    private void loadCache() {
        try  {
            String cache = "";
            if(Files.exists(Path.of(cacheFilePath))) {
                cache = Files.readString(Path.of(cacheFilePath), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                classLabelsByWords = gson.fromJson(cache, type);
            } else {
                classLabelsByWords = new LinkedTreeMap<>();
            }
        } catch (IOException e) {
            log.error("Loading cache failed!", e);
        }
    }

    public void addEntry(String classLabel, String markedText) {
        classLabelsByWords.put(markedText, classLabel);
    }

    public void saveCache() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                                         .create();
            String gsonString = gson.toJson(classLabelsByWords);
            if(!Files.exists(Path.of(cacheFilePath))) {
                Files.createFile(Path.of(cacheFilePath));
            }
            Files.writeString(
                    Path.of(cacheFilePath),
                    gsonString,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            log.error("Saving cache failed!", e);
        }
    }
}
