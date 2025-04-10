package ship_browser.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.lazywizard.console.Console;
import ship_browser.ui.ShipBrowserDialog;

public class OpenShipBrowserScript implements EveryFrameScript {
    private static final Logger log = Global.getLogger(OpenShipBrowserScript.class);
    static {
        log.setLevel(Level.ALL);
    }

    private boolean isDone = false;

    public OpenShipBrowserScript() {}

    @Override
    public boolean isDone()
    {
        return isDone;
    }

    @Override
    public boolean runWhilePaused()
    {
        return true;
    }

    @Override
    public void advance(float amount) {
        final CampaignUIAPI ui = Global.getSector().getCampaignUI();
        if (!isDone && !ui.isShowingDialog() && !ui.isShowingMenu())
        {
            isDone = true;

            try {
                ui.showInteractionDialog(new ShipBrowserDialog(), Global.getSector().getPlayerFleet());
            } catch (Exception ex) {
                // the code I copied this from said the game is bricked at this point and this does nothing but I'm leaving it
                log.info("Ship Browser: failed to open ship browser");
                log.info(ex);
                Global.getSector().getCampaignUI().getCurrentInteractionDialog().dismiss();
            }
        }
    }
}
