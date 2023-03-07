package ship_browser.ui;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import ship_browser.data.ModShipInfo;

public class ModShipsDialogPage implements DialogPage{
    private DialogPage parent = null;
    private InteractionDialogAPI dialog = null;
    private ModShipInfo shipInfo = null;

    public ModShipsDialogPage(final DialogPage parent, final ModShipInfo info, final InteractionDialogAPI dialog) {
        this.parent = parent;
        this.dialog = dialog;
        this.shipInfo = info;
    }

    @Override
    public void open() {

    }

    @Override
    public void optionSelected(final String optionText, final Object o) {

    }

    public String getMod() {
        return shipInfo.modId;
    }
}
