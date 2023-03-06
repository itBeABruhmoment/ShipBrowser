package ship_browser.data;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ModSpecAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import ship_browser.scripts.OpenShipBrowserScript;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ShipData {
    private static final Logger log = Global.getLogger(ShipData.class);
    static {
        log.setLevel(Level.ALL);
    }
    private static final ShipData instance = new ShipData();
    private static final String PATH_TO_SHIPS_CSV = "data/hulls/ship_data.csv";

    /**
     * Store info on ships in
     */
    public final HashMap<String, ModShipInfo> SHIP_DATA = new HashMap<>();

    private ModShipInfo createShipInfoFromCSV(final JSONArray csv) {
        final ModShipInfo shipInfoFromMod = new ModShipInfo();
        log.info("----------------------" + csv.length());
        for(int i = 0; i < csv.length(); i++) {
            JSONObject hullRow = null;
            try {
                hullRow = csv.getJSONObject(i);
            } catch (Exception e) {
                log.info(e);
                continue;
            }

            String hullId = null;
            try {
                hullId = hullRow.getString("id");
                final ShipAPI.HullSize hullSize = Global.getSettings().getHullSpec(hullId).getHullSize();
                switch (hullSize) {
                    case DESTROYER:
                        shipInfoFromMod.destroyers.add(hullId);
                        break;
                    case CRUISER:
                        shipInfoFromMod.cruisers.add(hullId);
                        break;
                    case CAPITAL_SHIP:
                        shipInfoFromMod.capitals.add(hullId);
                        break;
                    case FRIGATE:
                        shipInfoFromMod.frigates.add(hullId);
                }
            } catch (Exception e) {
                log.info(e);
            }
        }
        return shipInfoFromMod;
    }

    /**
     *
     * @param mod
     * @return can return null
     */
    private ModShipInfo createShipInfoForMod(ModSpecAPI mod) {
        final String modId = mod.getId();
        JSONArray csv = null;
        try {
            csv = Global.getSettings().loadCSV(PATH_TO_SHIPS_CSV, modId);
        } catch (Exception e) {
            log.info("Ship Browser: failed to load ships_data.csv for " + modId);
            log.info(e);
            return null;
        }
        final ModShipInfo info = createShipInfoFromCSV(csv);
        info.modId = modId;
        return info;
    }

    private ModShipInfo loadBaseGameShipInfo() {
        JSONArray csv = null;
        try {
            csv = Global.getSettings().loadCSV(PATH_TO_SHIPS_CSV);
        } catch (Exception e) {
            log.info("Ship Browser: failed to load ships_data.csv for base game");
            log.info(e);
            return null;
        }
        final ModShipInfo info = createShipInfoFromCSV(csv);
        info.modId = ModShipInfo.BASE_GAME_ID;
        return info;
    }

    public void loadData() {
        final ModShipInfo baseGameShipInfo = loadBaseGameShipInfo();
        if(baseGameShipInfo != null) {
            SHIP_DATA.put(ModShipInfo.BASE_GAME_ID, baseGameShipInfo);
        }

        final List<ModSpecAPI> enabledMods = Global.getSettings().getModManager().getEnabledModsCopy();
        for(final ModSpecAPI mod : enabledMods) {
            final ModShipInfo modShipInfo = createShipInfoForMod(mod);
            if(modShipInfo != null) {
                SHIP_DATA.put(modShipInfo.modId, modShipInfo);
            }
        }
        log.info(toString());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(final ModShipInfo info : SHIP_DATA.values()) {
           str.append(info).append('\n');
        }
        return str.toString();
    }

    public static ShipData getInstance() {
        return instance;
    }
}
