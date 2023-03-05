package ship_browser.console;

import com.fs.starfarer.api.Global;
import org.lazywizard.console.BaseCommand;
import ship_browser.scripts.OpenShipBrowserScript;

public class ShipBrowserCommand implements BaseCommand {

    @Override
    public CommandResult runCommand(String s, BaseCommand.CommandContext commandContext) {
        Global.getSector().addTransientScript(new OpenShipBrowserScript());
        return CommandResult.SUCCESS;
    }
}
