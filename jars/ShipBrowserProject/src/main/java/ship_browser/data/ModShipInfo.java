package ship_browser.data;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
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

    public String getGUIName() {
        try {
            if(modId.equals(BASE_GAME_ID)) {
                return "Vanilla";
            } else {
                return Global.getSettings().getModManager().getModSpec(modId).getName();
            }
        } catch (Exception e) {
            return modId + "error";
        }
    }

    public void addSpecToAppropriateCategory(ShipHullSpecAPI spec) {
        final ShipAPI.HullSize size = spec.getHullSize();
        final String id = spec.getHullId();
        switch (size) {
            case DESTROYER:
                destroyers.add(id);
                break;
            case CRUISER:
                cruisers.add(id);
                break;
            case CAPITAL_SHIP:
                capitals.add(id);
                break;
            case FRIGATE:
                frigates.add(id);
        }
    }

    public boolean isEmpty() {
        return frigates.isEmpty() && destroyers.isEmpty() && cruisers.isEmpty() && capitals.isEmpty();
    }

    public void merge(ModShipInfo other) {
        frigates.addAll(other.frigates);
        destroyers.addAll(other.destroyers);
        cruisers.addAll(other.cruisers);
        capitals.addAll(other.capitals);
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
