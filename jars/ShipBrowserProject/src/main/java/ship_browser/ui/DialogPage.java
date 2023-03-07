package ship_browser.ui;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;

public interface DialogPage {
    public void open();
    public void optionSelected(final String optionText, final Object o);
}
