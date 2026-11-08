package net.frank.explorertent;

import net.fabricmc.api.ModInitializer;
import net.frank.explorertent.tent.ModTentBlocks;
import net.frank.explorertent.tent.ModTentItems;
import net.frank.explorertent.tent.TentLegacyIds;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplorerTentMod implements ModInitializer {
    public static final String MOD_ID = "explorertent";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModTentBlocks.register();
        ModTentItems.register();
        TentLegacyIds.register();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
