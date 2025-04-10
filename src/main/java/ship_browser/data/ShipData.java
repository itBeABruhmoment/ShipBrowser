package ship_browser.data;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ModSpecAPI;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ShipData {
    private static final Logger log = Global.getLogger(ShipData.class);
    static {
        log.setLevel(Level.ALL);
    }
    private static final ShipData instance = new ShipData();
    private static final String PATH_TO_SHIPS_CSV = "data/hulls/ship_data.csv";
    private static final String PATH_TO_COPY_SHIPS_CSV = "data/hulls/copy_ship_data.csv";
    private static final String PATH_TO_DESC_CSV = "data/strings/descriptions.csv";
    public static final String MOD_ID = "bruh_ship_browser";

    /**
     * Store info on ships in
     */
    public final HashMap<String, ModShipInfo> SHIP_DATA = new HashMap<>();

    private ModShipInfo createShipInfoFromShipsCSV(final JSONArray csv) {
        final ModShipInfo shipInfoFromMod = new ModShipInfo();
        log.info("----------------------" + csv.length());
        for(int i = 0; i < csv.length(); i++) {
            JSONObject hullRow = null;
            try {
                hullRow = csv.getJSONObject(i);
            } catch (Exception e) {
                //log.info(e);
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
                //log.info(e);
            }
        }
        return shipInfoFromMod;
    }

    private ModShipInfo createShipInfoFromDescCSV(final JSONArray csv) {
        final ModShipInfo shipInfoFromMod = new ModShipInfo();
        log.info("----------------------" + csv.length());
        for(int i = 0; i < csv.length(); i++) {


            JSONObject hullRow = null;
            try {
                hullRow = csv.getJSONObject(i);
            } catch (Exception e) {
                //log.info(e);
                continue;
            }

            // move on if the description is not for a ship
            try {
                final String type = hullRow.getString("type");
                if(!type.equals("SHIP")) {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }

            String hullId = null;
            try {
                hullId = hullRow.getString("id");
                shipInfoFromMod.addSpecToAppropriateCategory(Global.getSettings().getHullSpec(hullId));
            } catch (Exception e) {
                //log.info(e);
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
        JSONArray csvShips = null;
        try {
            csvShips = Global.getSettings().loadCSV(PATH_TO_SHIPS_CSV, modId);
        } catch (Exception e) {
            log.info("Ship Browser: failed to load ships_data.csv for " + modId);
            log.info(e);
        }

        JSONArray csvDescriptions = null;
        try {
            csvDescriptions = Global.getSettings().loadCSV(PATH_TO_DESC_CSV, modId);
        } catch (Exception e) {
            log.info("Ship Browser: failed to load descriptions.csv for " + modId);
            log.info(e);
        }

        if(csvDescriptions == null && csvShips == null) {
            return null;
        } else if(csvDescriptions == null) {
            final ModShipInfo info = createShipInfoFromShipsCSV(csvShips);
            info.modId = modId;
            return info;
        } else if(csvShips == null) {
            final ModShipInfo info = createShipInfoFromDescCSV(csvDescriptions);
            info.modId = modId;
            return info;
        } else {
            final ModShipInfo info = createShipInfoFromShipsCSV(csvShips);
            info.merge(createShipInfoFromDescCSV(csvDescriptions));
            info.modId = modId;
            return info;
        }
    }

    private ModShipInfo loadBaseGameShipInfo() {
        JSONArray csv = null;
        try {
            csv = Global.getSettings().loadCSV(PATH_TO_COPY_SHIPS_CSV, MOD_ID);
        } catch (Exception e) {
            log.info("Ship Browser: failed to load ships_data.csv for base game");
            log.info(e);
            return null;
        }
        final ModShipInfo info = createShipInfoFromShipsCSV(csv);
        info.modId = ModShipInfo.BASE_GAME_ID;
        return info;
    }

    /**
     * find the mod of origin by trying to load its skin file (this is stupid)
     * @param spec
     * @return the mod of origin, or null of none can be found
     */
    private String findModOfSkin(final ShipHullSpecAPI spec) {
        final ShipHullSpecAPI parentHull = spec.getBaseHull();
        if(parentHull == null) {
            return null;
        }

        // extract the file name
        final String path = spec.getShipFilePath();
        int firstSlashIdx = path.length() - 1;
        for(; firstSlashIdx >= 0; firstSlashIdx--) {
            if(path.charAt(firstSlashIdx) == '/' || path.charAt(firstSlashIdx) == '\\') {
                break;
            }
        }
        if(firstSlashIdx < 0 || firstSlashIdx >= path.length() - 1) {
            return null;
        }

        final SettingsAPI settings = Global.getSettings();
        final String fileName = path.substring(firstSlashIdx + 1);
        String modId = null;
        try {
            settings.loadText("data/hulls/copy_skins/" + fileName, MOD_ID);
            modId = ModShipInfo.BASE_GAME_ID;
        } catch (Exception e) {

        }
        for(ModSpecAPI mod : settings.getModManager().getEnabledModsCopy()) {
            try {
                settings.loadText("data/hulls/skins/" + fileName, mod.getId());
                modId = mod.getId();
                break;
            } catch (Exception e) {

            }
        }

        return modId;
    }

    public void loadData() {
        SHIP_DATA.clear();

        final ModShipInfo baseGameShipInfo = loadBaseGameShipInfo();
        if(baseGameShipInfo != null) {
            SHIP_DATA.put(ModShipInfo.BASE_GAME_ID, baseGameShipInfo);
        }

        final List<ModSpecAPI> enabledMods = Global.getSettings().getModManager().getEnabledModsCopy();
        for(final ModSpecAPI mod : enabledMods) {
            final ModShipInfo modShipInfo = createShipInfoForMod(mod);
            if(modShipInfo != null && !modShipInfo.isEmpty()) {
                SHIP_DATA.put(modShipInfo.modId, modShipInfo);
            }
        }

        // try to figure out where skins are from
        for(ShipHullSpecAPI hullSpec : Global.getSettings().getAllShipHullSpecs()) {
            final String source = findModOfSkin(hullSpec);
            if(source != null) {
                ModShipInfo modShipInfo = SHIP_DATA.get(source);
                if(modShipInfo == null) {
                    modShipInfo = new ModShipInfo();
                    modShipInfo.modId = source;
                    SHIP_DATA.put(modShipInfo.modId, modShipInfo);
                }
                modShipInfo.addSpecToAppropriateCategory(hullSpec);
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
