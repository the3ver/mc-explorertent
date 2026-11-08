package net.frank.explorertent;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.DyeColor;

// Die 16 Vanilla-Farbstofffarben als RGB ohne Alpha; Kopie aus WorldExplorer
public final class DyeColors {

    public static final Map<String, Integer> BY_NAME;
    public static final List<Integer> ALL_RGB;

    static {
        Map<String, Integer> byName = new LinkedHashMap<>();
        for (DyeColor dye : DyeColor.values()) {
            byName.put(dye.getName(), rgb(dye));
        }
        BY_NAME = Collections.unmodifiableMap(byName);
        ALL_RGB = Arrays.stream(DyeColor.values()).map(DyeColors::rgb).toList();
    }

    private DyeColors() {}

    public static int rgb(DyeColor dye) {
        return dye.getTextureDiffuseColor() & 0xFFFFFF;
    }
}
