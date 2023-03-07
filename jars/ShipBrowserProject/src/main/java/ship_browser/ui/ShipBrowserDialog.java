package ship_browser.ui;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import org.graalvm.compiler.nodes.calc.IntegerDivRemNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ShipBrowserDialog implements InteractionDialogPlugin {
    private InteractionDialogAPI dialog = null;
    private OptionPanelAPI optionsPanel = null;
    private TextPanelAPI textPanel = null;
    private MainDialogPage mainPage = null;
    private DialogPage currentPage = null;
    @Override
    public void init(InteractionDialogAPI interactionDialogAPI) {
        this.dialog = interactionDialogAPI;
        this.optionsPanel = dialog.getOptionPanel();
        this.textPanel = dialog.getTextPanel();

        dialog.hideVisualPanel();
        mainPage = new MainDialogPage(dialog);
        currentPage = mainPage;
        currentPage.open();
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {
        if(mainPage.ended()) {
            dialog.dismiss();
        }

        if(optionData instanceof DialogPage) {
            currentPage = (DialogPage) optionData;
            currentPage.optionSelected(optionText, optionData);
        } else {
            currentPage.optionSelected(optionText, optionData);
        }
    }

    @Override
    public void optionMousedOver(String s, Object o) {

    }

    @Override
    public void advance(float v) {

    }

    @Override
    public void backFromEngagement(EngagementResultAPI engagementResultAPI) {

    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }
}
