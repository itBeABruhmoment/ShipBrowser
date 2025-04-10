package ship_browser.ui;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;

/**
 * If one of these is set as an option it is navigated to, otherwise the option selected callback is called
 */
public interface DialogPage {
    public void open();
    public void optionSelected(final String optionText, final Object o);
}
