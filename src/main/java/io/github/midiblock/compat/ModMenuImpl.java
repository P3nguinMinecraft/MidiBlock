package io.github.midiblock.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.midiblock.config.ConfigScreen;


public class ModMenuImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<ConfigScreen> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }
}
