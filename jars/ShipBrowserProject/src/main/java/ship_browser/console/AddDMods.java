package ship_browser.console;

import java.util.List;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;

import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class AddDMods implements BaseCommand
{
    private void addDmods(FleetMemberAPI member, int numDMods)
    {
        if(member.getType() == FleetMemberType.SHIP)
        {
            Console.showMessage("adding hullsmods to the " + member.getShipName());
            DModManager.addDMods(member, true, numDMods, new Random());
        }
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

        if(individualArgs[0].length() == 0 || individualArgs[1].length() == 0)
        {
            return CommandResult.BAD_SYNTAX;
        }

        String shipSpecification = individualArgs[0];
        int numDMods = Integer.parseInt(individualArgs[1]);

        List<FleetMemberAPI> members = Global.getSector().getPlayerFleet().getMembersWithFightersCopy();
        if(shipSpecification.equals("all"))
        {
            //Console.showMessage("path1");
            for(FleetMemberAPI member : members)
            {
                addDmods(member, numDMods);
            }
        }
        else if(shipSpecification.indexOf("-") == -1)
        {
            //Console.showMessage("path2");
            int memberNumber = Integer.parseInt(shipSpecification);
            addDmods(members.get(memberNumber -1), numDMods);
        }
        else if(shipSpecification.indexOf("-") != -1)
        {
            Console.showMessage("path3");
            String[] bounds = shipSpecification.split("-");
            int lowerBound = Integer.parseInt(bounds[0]) - 1;
            int upperBound = Integer.parseInt(bounds[1]) - 1;
            Console.showMessage(lowerBound + " " + upperBound);

            if(lowerBound < 0 || upperBound > members.size() - 1 || lowerBound > upperBound)
            {
                Console.showMessage("invalid ship selection");
                return CommandResult.BAD_SYNTAX;
            }

            for(int i = lowerBound; i <= upperBound; i++)
            {
                addDmods(members.get(i), numDMods);
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