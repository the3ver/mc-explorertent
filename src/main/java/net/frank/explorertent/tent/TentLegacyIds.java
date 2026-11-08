package net.frank.explorertent.tent;

import java.util.List;
import net.fabricmc.fabric.api.event.registry.FabricRegistry;
import net.frank.explorertent.ExplorerTentMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * Bis WorldExplorer 0.12 lag das Zelt unter mc3ver:*. Die Aliase lassen alte Welten und Inventare mit dem
 * eigenständigen Mod laden; beim nächsten Speichern schreibt das Spiel die neuen IDs. Sie bleiben dauerhaft.
 */
public final class TentLegacyIds {

    static final String LEGACY_NAMESPACE = "mc3ver";

    private TentLegacyIds() {}

    public static void register() {
        alias(
                BuiltInRegistries.BLOCK,
                List.of("tent_canvas", "tent_pole", "tent_sleeping_bag", "tent_furnace", "tent_flap", "tent_chest"));
        alias(BuiltInRegistries.ITEM, List.of("explorer_tent"));
        alias(BuiltInRegistries.BLOCK_ENTITY_TYPE, List.of("tent_chest", "tent_furnace"));
    }

    private static void alias(Registry<?> registry, List<String> paths) {
        for (String path : paths) {
            ((FabricRegistry) registry)
                    .addAlias(Identifier.fromNamespaceAndPath(LEGACY_NAMESPACE, path), ExplorerTentMod.id(path));
        }
    }
}
