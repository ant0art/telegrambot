package bot.dompp.storage;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonElement;

public abstract class BotLibrary {
    List<BotLibrary> botLibraryObjects = new ArrayList<>();
    
    protected BotLibrary() {
        botLibraryObjects.add(this);
    }
    
    public static JsonElement getParser() {
        return null;
    }

    public abstract BotLibrary copy(); 
    
}
