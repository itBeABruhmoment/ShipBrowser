package ship_browser.ui;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import ship_browser.data.ModShipInfo;
import ship_browser.data.ShipData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainDialogPage implements DialogPage{
    private InteractionDialogAPI dialog = null;
    private ArrayList<ModShipsDialogPage> options = new ArrayList<>();
    private boolean shouldEnd = false;
    private int currentPage = 0;
    private static final int MAX_MODS_PER_PAGE = 6;
    private static final String EXIT = "exit";
    private static final String NEXT_PAGE = "next page";
    private static final String PREV_PAGE = "prev page";

    public MainDialogPage(final InteractionDialogAPI dialog) {
        this.dialog = dialog;
        final ShipData shipsByMod = ShipData.getInstance();
        for(final ModShipInfo info : shipsByMod.SHIP_DATA.values()) {
            options.add(new ModShipsDialogPage(this, info, dialog));
        }
        Collections.sort(options, new ModShipsPageComparator());
    }

    @Override
    public void open() {
        final OptionPanelAPI optionsPanel = dialog.getOptionPanel();
        optionsPanel.clearOptions();
        // originally passed in this as second param but the game didn't like that
        optionsPanel.addOption(EXIT, PAGE_OPTIONS.EXIT);
        optionsPanel.addOption(NEXT_PAGE, PAGE_OPTIONS.NEXT);
        optionsPanel.addOption(PREV_PAGE, PAGE_OPTIONS.PREV);
        for(int i = currentPage * MAX_MODS_PER_PAGE; i < MAX_MODS_PER_PAGE && i < options.size(); i++) {
            final ModShipsDialogPage page = options.get(i);
            optionsPanel.addOption(page.getMod(), page);
        }
    }

    @Override
    public void optionSelected(final String optionText, final Object o) {
        if(o instanceof PAGE_OPTIONS) { // exit, next page, or prev page chosen
            if(optionText.equals(EXIT)) {
                shouldEnd = true;
                open();
            } else if(optionText.equals(NEXT_PAGE)) {
                currentPage++;
                open();
            } else if(optionText.equals(PREV_PAGE)) {
                if(currentPage > 0) {
                    currentPage--;
                }
                open();
            }
        } else { // option to view ships of faction chosen
            ((ModShipsDialogPage) o).open();
        }
    }

    public boolean ended() {
        return shouldEnd;
    }

    class ModShipsPageComparator implements Comparator<ModShipsDialogPage> {
        @Override
        public int compare(ModShipsDialogPage a, ModShipsDialogPage b) {
            return a.getMod().compareToIgnoreCase(b.getMod());
        }
    }

    enum PAGE_OPTIONS {
        EXIT,
        PREV,
        NEXT
    }
}
