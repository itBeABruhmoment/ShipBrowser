package ship_browser.console;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class GetCurrentSystemTags implements BaseCommand
{
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        final StarSystemAPI system = (StarSystemAPI) Global.getSector().getCurrentLocation();

        if (system.isHyperspace()) {
            Console.showMessage("Error: This command cannot be used in hyperspace.");
            return CommandResult.WRONG_CONTEXT;
        }

        SectorEntityToken yourFleet = Global.getSector().getPlayerFleet();
        StarSystemAPI currentSystem = (StarSystemAPI)yourFleet.getContainingLocation();
        Console.showMessage(currentSystem.getBaseName() + ": " + currentSystem.getTags());
        return CommandResult.SUCCESS;
    }
}
