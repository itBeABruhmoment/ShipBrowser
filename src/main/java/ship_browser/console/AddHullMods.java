package ship_browser.console;

import java.util.List;
import java.util.Collection;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class AddHullMods implements BaseCommand{
    /*
    private void test1()
    {
        List<FleetMemberAPI> members = Global.getSector().getPlayerFleet().getMembersWithFightersCopy();
        for(FleetMemberAPI memeber : members)
        {
            Console.showMessage(memeber.getSpecId() + " " + memeber.getShipName());
        }
    }

    public void test2()
    {
        List<FleetMemberAPI> members = Global.getSector().getPlayerFleet().getMembersWithFightersCopy();
        FleetMemberAPI first = members.get(0);
        ShipVariantAPI variantCopy = first.getVariant().clone();
        variantCopy.addPermaMod("heavyarmor", true);
        first.setVariant(variantCopy, true, true);
    }
    */
    public void addHullMods(FleetMemberAPI ship, String[] hullMods)
    {
        if(ship.getType() == FleetMemberType.SHIP)
        {
            //Console.showMessage("adding hullsmods to the " + ship.getShipName());
            ShipVariantAPI variant = ship.getVariant().clone();
            Collection<String> allReadyExistingHullmods = variant.getHullMods();
            for(String hullMod : hullMods)
            {
                if(!allReadyExistingHullmods.contains("hullMod"))
                {
                    variant.addPermaMod(hullMod);
                }
            }
            ship.setVariant(variant, true, true);
        }
    }

    public boolean isValidHullMod(String id)
    {
        List<HullModSpecAPI> hullMods = Global.getSettings().getAllHullModSpecs();
        for(HullModSpecAPI hullMod : hullMods)
        {
            if(id.equals(hullMod.getId()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        args = args.toLowerCase();
        String[] individualArgs = args.split(" ");

        if(individualArgs.length != 2)
        {
            Console.showMessage("this command requires exactly 2 arguements. Note that characters separted spaces are processed as arguments");
            return CommandResult.BAD_SYNTAX;
        }

        if(individualArgs[0].length() == 0 || individualArgs[1].length() <= 2)
        {
            return CommandResult.BAD_SYNTAX;
        }

        individualArgs[1] = individualArgs[1].substring(1, individualArgs[1].length() - 1);
        String[] hullModIds = individualArgs[1].split(",");
        String shipSpecification = individualArgs[0];

        for (String hullModId : hullModIds) {
            if(!isValidHullMod(hullModId))
            {
                Console.showMessage("no hullmod with the id \"" + hullModId + "\" was found");
                return CommandResult.BAD_SYNTAX;
            }
        }

        List<FleetMemberAPI> members = Global.getSector().getPlayerFleet().getMembersWithFightersCopy();
        if(shipSpecification.equals("all"))
        {
            //Console.showMessage("path1");
            for(FleetMemberAPI member : members)
            {
                addHullMods(member, hullModIds);
            }
        }
        else if(shipSpecification.indexOf("-") == -1)
        {
            //Console.showMessage("path2");
            int memberNumber = Integer.parseInt(shipSpecification);
            addHullMods(members.get(memberNumber -1), hullModIds);
        }
        else if(shipSpecification.indexOf("-") != -1)
        {
            //Console.showMessage("path3");
            String[] bounds = shipSpecification.split("-");
            int lowerBound = Integer.parseInt(bounds[0]) - 1;
            int upperBound = Integer.parseInt(bounds[1]) - 1;
            //Console.showMessage(lowerBound + " " + upperBound);

            if(lowerBound < 0 || upperBound > members.size() - 1 || lowerBound > upperBound)
            {
                Console.showMessage("invalid ship selection");
                return CommandResult.BAD_SYNTAX;
            }

            for(int i = lowerBound; i <= upperBound; i++)
            {
                addHullMods(members.get(i), hullModIds);
            }
        }
        else
        {
            Console.showMessage("ship selection has unrecognised syntax");
            return CommandResult.BAD_SYNTAX;
        }

        return CommandResult.SUCCESS;

    }
}
