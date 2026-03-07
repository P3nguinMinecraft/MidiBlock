package io.github.blocknroll.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.blocknroll.gui.MidiDropScreen;


public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<MidiDropScreen> getModConfigScreenFactory() {
        return MidiDropScreen::new;
    }
}
