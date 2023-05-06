package ship_browser.console;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;

import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class CloneShip implements BaseCommand
{

    // code from lazy wizard
    private boolean cloneShip(FleetMemberAPI cloneCandidate, int amount)
    {
        String variantId = cloneCandidate.getVariant().getHullSpec().getHullId();
        String variant = null;
        for (String id : Global.getSettings().getAllVariantIds())
        {
            if (variantId.equalsIgnoreCase(id))
            {
                variant = id;
                break;
            }
        }

        // Test for empty hulls
        if (variant == null)
        {
            final String withHull = variantId + "_Hull";
            for (String id : Global.getSettings().getAllVariantIds())
            {
                if (withHull.equalsIgnoreCase(id))
                {
                    variant = id;
                    break;
                }
            }
        }

        if(variantId == null)
        {
            return false;
        }

        // We've finally verified the variant id, now create the actual ship
        FleetMemberAPI ship;
        try
        {
            ship = Global.getFactory().createFleetMember(FleetMemberType.SHIP, variant);
        }
        catch (Exception ex)
        {
            Console.showException("Failed to create variant '" + variant + "'!", ex);
            return false;
        }

        final FleetDataAPI fleet = Global.getSector().getPlayerFleet().getFleetData();
        for (int i = 0; i < amount; i++)
        {
            ship = Global.getFactory().createFleetMember(FleetMemberType.SHIP, cloneCandidate.getVariant().clone());
            FleetEncounterContext.prepareShipForRecovery(ship, true, true, false,1f, 1f, MathUtils.getRandom());
            for(String smod : cloneCandidate.getVariant().getSMods())
            {
                ship.getVariant().addPermaMod(smod, true);
            }
            ship.updateStats(); //seems to work fine without this function, but I saw this used in some code to add smods
            fleet.addFleetMember(ship);
        }
        return true;
    }


    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        String[] individualArgs = args.split(" ");

        if(individualArgs.length > 2 || individualArgs.length == 0)
        {
            Console.showMessage("this command accepts 1 or 2 arguments");
            return CommandResult.BAD_SYNTAX;
        }

        int shipInFleetNumber = 0;
        int numClones = 0;
        try
        {
            shipInFleetNumber = Integer.parseInt(individualArgs[0]);
            if(individualArgs.length == 1)
            {
                numClones = 1;
            }
            else
            {
                numClones = Integer.parseInt(individualArgs[1]);
            }
        }
        catch(NumberFormatException exception)
        {
            Console.showMessage("invalid number");
            return CommandResult.BAD_SYNTAX;
        }

        if(numClones < 1)
        {
            Console.showMessage("invalid number of clones");
            return CommandResult.BAD_SYNTAX;
        }

        List<FleetMemberAPI> members = Global.getSector().getPlayerFleet().getMembersWithFightersCopy();
        FleetMemberAPI cloneCandidate = null;
        try
        {
            cloneCandidate = members.get(shipInFleetNumber - 1);
        }
        catch(IndexOutOfBoundsException exception)
        {
            Console.showMessage("your fleet does not have a ship " + shipInFleetNumber);
            return CommandResult.BAD_SYNTAX;
        }

        if(cloneCandidate.getType() != FleetMemberType.SHIP)
        {
            Console.showMessage("member " + shipInFleetNumber + " of you fleet is not a ship");
            return CommandResult.BAD_SYNTAX;
        }

        boolean successful = cloneShip(cloneCandidate, numClones);
        if(!successful)
        {
            Console.showMessage("could not create hull of type \"" + cloneCandidate.getVariant().getHullSpec().getHullId() + "\"");
            return CommandResult.BAD_SYNTAX;
        }

        return CommandResult.SUCCESS;

    }
}
