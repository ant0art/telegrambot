package bot.dompp.storage;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.*;
import bot.dompp.storage.MyPath;

public class MyJsonParser {
    private String path;
    private Map<String, JsonArray> possibleRequests = new HashMap<>();
    private Logger logger = LoggerFactory.getLogger(MyJsonParser.class);


    /**
     * Constructor
     * 
     * @param path set the path of known data
     */
    public MyJsonParser(String path) {
        this.path = path;
    }

    /**
     * @return the possibleRequests
     */
    public Map<String, JsonArray> getPossibleRequests(String string) {
        JsonObject parser = HomeData.getParser().getAsJsonObject();
        logger.info("parser got");

        for (Map.Entry<String, JsonElement> entry : parser.entrySet()) {
            logger.info("Catch the pair key-value");
            // значение
            JsonElement vJsonElement = entry.getValue();
            logger.info("JsonElement got");

            JsonArray keyArray = vJsonElement.getAsJsonObject().get(string).getAsJsonArray();
            logger.info("keys in Array");

            String title = vJsonElement.getAsJsonObject().get("title").getAsString();
            logger.info("title as String");

            possibleRequests.put(title, keyArray);
        }

        return possibleRequests;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    public JsonElement getParser() {
        Reader reader;
        JsonElement parser = null;
        try {
            // собираем данные по востребованным объектам из файла
            reader = Files.newBufferedReader(Path.of(path).toAbsolutePath());
            // парсим все данные как один объект
            parser = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parser;
    }
}
