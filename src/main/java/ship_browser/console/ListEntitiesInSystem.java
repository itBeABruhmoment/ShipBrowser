package ship_browser.console;

import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class ListEntitiesInSystem implements BaseCommand
{

    @Override
    public CommandResult runCommand(String args, CommandContext context)
    {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        SectorEntityToken yourFleet = Global.getSector().getPlayerFleet();
        StarSystemAPI currentSystem = (StarSystemAPI)yourFleet.getContainingLocation();
        List<SectorEntityToken> sectorEntityTokens = currentSystem.getAllEntities();
        for(SectorEntityToken entity : sectorEntityTokens)
        {
            String message = entity + " | fullName: " + entity.getFullName() +
                    " | customEntitiyType: " + entity.getCustomEntityType();
            Console.showMessage(message);
        }

        return CommandResult.SUCCESS;
    }
}
