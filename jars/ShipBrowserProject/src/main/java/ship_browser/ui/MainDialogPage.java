package ship_browser.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import ship_browser.data.ModShipInfo;
import ship_browser.data.ShipData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainDialogPage implements DialogPage{
    private static final Logger log = Global.getLogger(MainDialogPage.class);
    static {
        log.setLevel(Level.ALL);
    }
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
            log.info(info.modId);
        }
        Collections.sort(options, new ModShipsPageComparator());
    }

    @Override
    public void open() {
        final OptionPanelAPI optionsPanel = dialog.getOptionPanel();
        optionsPanel.clearOptions();
        // originally passed in this as second param but the game didn't like that
        optionsPanel.addOption(EXIT, NonModOptions.EXIT);
        optionsPanel.addOption(NEXT_PAGE, NonModOptions.NEXT);
        optionsPanel.addOption(PREV_PAGE, NonModOptions.PREV);
        for(int i = currentPage * MAX_MODS_PER_PAGE; i < MAX_MODS_PER_PAGE * (currentPage + 1) && i < options.size(); i++) {
            final ModShipsDialogPage page = options.get(i);
            optionsPanel.addOption(page.getMod(), page);
        }
    }

    @Override
    public void optionSelected(final String optionText, final Object o) {
        if(o instanceof NonModOptions) { // exit, next page, or prev page chosen
            if(optionText.equals(EXIT)) {
                shouldEnd = true;
            } else if(optionText.equals(NEXT_PAGE)) {
                if(options.size() - currentPage * MAX_MODS_PER_PAGE > 0) {
                    log.info(options.size());
                    log.info(currentPage);
                    log.info(currentPage * MAX_MODS_PER_PAGE);
                    log.info(options.size() - currentPage * MAX_MODS_PER_PAGE);
                    currentPage++;
                }
                open();
            } else if(optionText.equals(PREV_PAGE)) {
                if(currentPage > 0) {
                    currentPage--;
                }
                open();
            }
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

    enum NonModOptions {
        EXIT,
        PREV,
        NEXT
    }
}
