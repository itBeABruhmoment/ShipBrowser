package ship_browser.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetMemberPickerListener;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ship_browser.data.ModShipInfo;

import java.util.*;

public class ModShipsDialogPage implements DialogPage, FleetMemberPickerListener {
    private static final Logger log = Global.getLogger(ModShipsDialogPage.class);
    static {
        log.setLevel(Level.ALL);
    }

    private DialogPage parent = null;
    private InteractionDialogAPI dialog = null;
    private ModShipInfo shipInfo = null;

    public ModShipsDialogPage(final DialogPage parent, final ModShipInfo info, final InteractionDialogAPI dialog) {
        this.parent = parent;
        this.dialog = dialog;
        this.shipInfo = info;
    }

    @Override
    public void open() {
        final OptionPanelAPI options = dialog.getOptionPanel();
        options.clearOptions();
        options.addOption("Back", parent);
        options.addOption("Frigates", DialogOptions.FRIGATES);
        options.addOption("Destroyers", DialogOptions.DESTROYERS);
        options.addOption("Cruisers", DialogOptions.CRUISERS);
        options.addOption("Capitals", DialogOptions.CAPITALS);
    }

    @Override
    public void optionSelected(final String optionText, final Object o) {
        if(o instanceof DialogOptions) {
            final ArrayList<FleetMemberAPI> toShow = createMembers(shipInfo, (DialogOptions) o);

            dialog.showFleetMemberPickerDialog(
                    "ships",
                    "take",
                    "exit",
                    5,
                    9,
                    96,
                    true,
                    true,
                    toShow,
                    this
            );
        }
    }

    public String getMod() {
        return shipInfo.getGUIName();
    }

    public ArrayList<FleetMemberAPI> createMembers(final ModShipInfo info, final DialogOptions shipSize) {
        final ArrayList<FleetMemberAPI> members = new ArrayList<>();
        Collection<String> ids = null;
        switch (shipSize) {
            case FRIGATES:
                ids = info.frigates;
                break;
            case DESTROYERS:
                ids = info.destroyers;
                break;
            case CRUISERS:
                ids = info.cruisers;
                break;
            default:
                ids = info.capitals;
                break;
        }
        for(final String hullId : ids) {
            final FleetMemberAPI memberAPI = createMember(hullId);
            if(memberAPI != null) {
                members.add(memberAPI);
            }
        }
        Collections.sort(members, new CompareFleetMembers());
        return members;
    }

    /**
     *
     * @param hullId
     * @return can return null
     */
    // code from console commands mod
    public FleetMemberAPI createMember(String hullId) {
        // Test for variants
        String variant = null;
        for (String id : Global.getSettings().getAllVariantIds()) {
            if (hullId.equalsIgnoreCase(id)) {
                variant = id;
                break;
            }
        }

        // Test for empty hulls
        if (variant == null) {
            final String withHull = hullId + "_Hull";
            for (String id : Global.getSettings().getAllVariantIds()) {
                if (withHull.equalsIgnoreCase(id)) {
                    variant = id;
                    break;
                }
            }
        }

        // Before we give up, maybe the .variant file doesn't match the ID?
        if (variant == null) {
            try {
                variant = Global.getSettings().loadJSON("data/variants/"
                        + hullId + ".variant").getString("variantId");
            } catch (Exception ex) {
                return null;
            }
        }

        // We've finally verified the variant id, now create the actual ship
        FleetMemberAPI ship;
        try {
            ship = Global.getFactory().createFleetMember(FleetMemberType.SHIP, variant);
            ship.setShipName(hullId);
            return ship;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void pickedFleetMembers(List<FleetMemberAPI> list) {
        for(final FleetMemberAPI member : list) {

        }
        log.info("picked script");
    }

    @Override
    public void cancelledFleetMemberPicking() {
        log.info("cancelled script");
    }

    enum DialogOptions {
        FRIGATES,
        DESTROYERS,
        CRUISERS,
        CAPITALS
    }

    private static class CompareFleetMembers implements Comparator<FleetMemberAPI> {
        @Override
        public int compare(FleetMemberAPI o1, FleetMemberAPI o2) {
            return o1.getShipName().compareToIgnoreCase(o2.getShipName());
        }
    }
}
