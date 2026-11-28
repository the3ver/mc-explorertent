package net.frank.explorertent.tent;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TentHeightmapTagTest {

    // Seit 26.3 zählt für die Regen-Heightmap MOTION_BLOCKING nicht mehr forceSolidOn(), sondern dieser Block-Tag.
    // Fehlt die Plane darin, regnet es durch das ganze Zelt bis auf den Boden.
    @ParameterizedTest
    @ValueSource(strings = {"blocks_motion_in_heightmap", "blocks_motion_in_heightmap_no_leaves"})
    void tentCanvasStopsRainInTheHeightmap(String tag) throws Exception {
        Path file = Paths.get("")
                .toAbsolutePath()
                .resolve("src/main/resources/data/minecraft/tags/block/" + tag + ".json");
        assertTrue(Files.exists(file), "Tag-Datei fehlt: " + file);
        List<String> values = new ArrayList<>();
        for (JsonElement value : JsonParser.parseString(Files.readString(file))
                .getAsJsonObject()
                .getAsJsonArray("values")) {
            values.add(value.getAsString());
        }
        assertTrue(values.contains("explorertent:tent_canvas"), tag + " enthält die Zeltplane nicht: " + values);
    }
}
