package ship_browser.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.WeaponGroupSpec;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SaveVariant implements BaseCommand{
    public static JSONObject toJson(final ShipVariantAPI variant, final String variantId) throws JSONException {
        final JSONObject json = new JSONObject();
        json.put("displayName", variant.getDisplayName());
        json.put("fluxCapacitors",variant.getNumFluxCapacitors());
        json.put("fluxVents", variant.getNumFluxVents());
        json.put("goalVariant", false);
        json.put("hullId", variant.getHullSpec().getHullId());
        json.put("hullMods", variant.getNonBuiltInHullmods());
        json.put("sMods", variant.getSMods());

        // sMods are put in the perma mod list
        final HashSet<String> sMods = new HashSet<>();
        sMods.addAll(variant.getSMods());
        final ArrayList<String> permaMods = new ArrayList<>();
        for(final String permaMod : permaMods) {
            if(!sMods.contains(permaMod)) {
                permaMods.add(permaMod);
            }
        }
        json.put("permaMods", permaMods);

        json.put("variantId", variantId);

        final JSONArray weaponGroups = new JSONArray();
        for(final WeaponGroupSpec spec : variant.getWeaponGroups()) {
            final JSONObject weaponGroup = new JSONObject();
            weaponGroup.put("autofire", spec.isAutofireOnByDefault());
            weaponGroup.put("mode", spec.getType().toString());
            final JSONObject weaponSlots = new JSONObject();
            for(final String slot : spec.getSlots()) {
                weaponSlots.put(slot, variant.getWeaponId(slot));
            }
            weaponGroup.put("weapons", weaponSlots);
            weaponGroups.put(weaponGroup);
        }
        json.put("weaponGroups", weaponGroups);

        return json;
    }

    @Override
    public BaseCommand.CommandResult runCommand(String args, BaseCommand.CommandContext context)
    {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return BaseCommand.CommandResult.WRONG_CONTEXT;
        }

        String[] individualArgs = args.split(" ");

        if(individualArgs.length != 2) {
            Console.showMessage("this command requires exactly 2 arguments. Note that characters separated spaces are processed as arguments");
            return BaseCommand.CommandResult.BAD_SYNTAX;
        }

        if(individualArgs[0].isEmpty() || individualArgs[1].isEmpty()) {
            return BaseCommand.CommandResult.BAD_SYNTAX;
        }

        final int shipInFleetToSave = Integer.parseInt(individualArgs[0]);
        final String variantId = individualArgs[1];

        if(shipInFleetToSave < 1) {
            return BaseCommand.CommandResult.BAD_SYNTAX;
        }

        final List<FleetMemberAPI> members = Global.getSector().getPlayerFleet().getMembersWithFightersCopy();
        JSONObject json = null;
        try {
            json = toJson(members.get(shipInFleetToSave - 1).getVariant(), variantId);
            Global.getSettings().writeTextFileToCommon(variantId + ".variant", json.toString(8));
            Console.showMessage(json.toString(8));
        } catch (Exception e) {
            Console.showMessage(e);
            return CommandResult.ERROR;
        }
        return BaseCommand.CommandResult.SUCCESS;
    }
}
