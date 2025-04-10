package ship_browser.console;

import java.util.Vector;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class GetNumConstellations implements BaseCommand
{
    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        // yes, this is awful for what should be a O(1) operation, but I can't find a method to get
        // some sort of list of the sector's constellations
        Vector<String> constellationNames = new Vector<String>();
        for(StarSystemAPI system : Global.getSector().getStarSystems())
        {
            if(system.getConstellation() != null
                    && system.getConstellation().getName() != null
                    && constellationNames.indexOf(system.getConstellation().getName()) == -1)
            {
                constellationNames.add(system.getConstellation().getName());
            }
        }
        for(String s : constellationNames)
        {
            Console.showMessage(s);
        }
        Console.showMessage("there are " + constellationNames.size() + " constellations");
        return CommandResult.SUCCESS;
    }
}