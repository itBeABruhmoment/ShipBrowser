package ship_browser.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import ship_browser.data.ShipData;

/**
 * Notes
 * Code that might be useful
 *  exerelin.utilities.versionchecker.UpdateNotificationScript
 *  exerelin.utilities.versionchecker.VCModPlugin
 *  com.fs.starfarer.api.impl.campaign.rulecmd.salvage
 */


public class ShipBrowserModPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        ShipData.getInstance().loadData();
    }
}
