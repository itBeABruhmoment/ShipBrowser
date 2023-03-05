package ship_browser.ui;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import org.graalvm.compiler.nodes.calc.IntegerDivRemNode;

import java.awt.*;
import java.util.Map;

public class ShipBrowserDialog implements InteractionDialogPlugin {
    private InteractionDialogAPI dialog = null;
    private OptionPanelAPI optionsPanel = null;
    private TextPanelAPI textPanel = null;

    private enum Options {
        MAIN,
        EXIT
    }
    @Override
    public void init(InteractionDialogAPI interactionDialogAPI) {
        this.dialog = interactionDialogAPI;
        this.optionsPanel = dialog.getOptionPanel();
        this.textPanel = dialog.getTextPanel();

        dialog.hideVisualPanel();
        goToOption(Options.MAIN);
    }

    private void goToOption(Options option) {
        optionsPanel.clearOptions();
        switch (option) {
            case MAIN:
                textPanel.addPara("Test paragraph");
                // And give them some options on what to do next
                optionsPanel.addOption("Exit", Options.EXIT);
                break;
            case EXIT:
                dialog.dismiss();
                break;
        }
    }
    @Override
    public void optionSelected(String optionText, Object optionData)
    {
        textPanel.addParagraph(optionText, Color.CYAN);
        if (optionData instanceof Options) {
            goToOption(((Options) optionData));
        } else {
            goToOption(Options.EXIT);
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
