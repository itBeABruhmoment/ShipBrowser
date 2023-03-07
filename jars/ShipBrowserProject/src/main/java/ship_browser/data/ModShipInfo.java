package ship_browser.data;

import com.fs.starfarer.api.Global;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

public class ModShipInfo {
    private static final Logger log = Global.getLogger(ModShipInfo.class);
    static {
        log.setLevel(Level.ALL);
    }

    public static final String BASE_GAME_ID = "ship_browser_base_game";
    public String modId = "";
    public HashSet<String> frigates = new HashSet<>();
    public HashSet<String> destroyers = new HashSet<>();
    public HashSet<String> cruisers = new HashSet<>();
    public HashSet<String> capitals = new HashSet<>();

    public boolean isEmpty() {
        return frigates.isEmpty() && destroyers.isEmpty() && cruisers.isEmpty() && capitals.isEmpty();
    }

    @Override
    public String toString() {
        final JSONObject json = new JSONObject();
        try {
            json.put("mod", modId);
            json.put("frigates", frigates);
            json.put("destroyers", destroyers);
            json.put("cruisers", cruisers);
            json.put("capitals", capitals);
        } catch (Exception e) {
            log.info("Ship Browser: Error in toString");
            log.info(e);
        }
        return json.toString();
    }
}
