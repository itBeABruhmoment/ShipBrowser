package ship_browser.console;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class FindAllEntitiesByType implements BaseCommand
{
    private int searchLocation(LocationAPI location, String Name)
    {
        int resultsInSystem = 0;
        List<SectorEntityToken> entities = location.getAllEntities();
        for(SectorEntityToken entity : entities)
        {
            if(entity.getCustomEntityType() != null && entity.getCustomEntityType().equals(Name))
            {
                resultsInSystem++;
            }
        }
        return resultsInSystem;
    }

    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        if(args.length() == 0)
        {
            Console.showMessage("input an argument");
            return CommandResult.BAD_SYNTAX;
        }

        int countResults = 0;
        for(StarSystemAPI system : Global.getSector().getStarSystems())
        {
            int resultsInSystem = searchLocation(system, args);
            if(resultsInSystem > 0)
            {
                Console.showMessage(system.getBaseName() + ": " + resultsInSystem + " results");
                countResults += resultsInSystem;
            }
        }

        LocationAPI hyperspace = Global.getSector().getHyperspace();
        int resultsInHyperspace = searchLocation(hyperspace, args);
        if(resultsInHyperspace > 0)
        {
            Console.showMessage("hyperspace: " + resultsInHyperspace + " results");
            countResults += resultsInHyperspace;
        }

        Console.showMessage("found " + countResults + " \"" + args + "\" in total");
        return CommandResult.SUCCESS;
    }
}
