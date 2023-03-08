package ship_browser.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class FindSystemWithTags implements BaseCommand
{
    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        String[] tags = args.split("\\s+");
        if(tags.length == 0)
        {
            Console.showMessage("input at least 1 tag as an argument");
            return CommandResult.BAD_SYNTAX;
        }

        int countResults = 0;
        for(StarSystemAPI system : Global.getSector().getStarSystems())
        {
            boolean hasTags = true;
            for(String tag : tags)
            {
                if(!system.hasTag(tag))
                {
                    hasTags = false;
                    break;
                }
            }

            if(hasTags)
            {
                Console.showMessage(system.getBaseName() + " system tags:" + system.getTags());
                countResults++;
            }
        }
        Console.showMessage("found " + countResults + " systems with specified tags");
        return CommandResult.SUCCESS;
    }
}
