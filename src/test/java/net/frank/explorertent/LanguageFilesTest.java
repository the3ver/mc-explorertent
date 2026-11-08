package net.frank.explorertent;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class LanguageFilesTest {

    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();

    private Map<String, String> loadLangFile(String filename) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("assets/explorertent/lang/" + filename);
        assertNotNull(stream, "Language file assets/explorertent/lang/" + filename + " must exist");
        return GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), MAP_TYPE);
    }

    @Test
    void testLanguageFiles_ContainAllRequiredKeysAndMatch() {
        Map<String, String> enUs = loadLangFile("en_us.json");
        Map<String, String> deDe = loadLangFile("de_de.json");

        Set<String> requiredKeys = Set.of(
                "item.explorertent.explorer_tent",
                "block.explorertent.tent_canvas",
                "block.explorertent.tent_pole",
                "block.explorertent.tent_sleeping_bag",
                "block.explorertent.tent_furnace",
                "block.explorertent.tent_flap",
                "block.explorertent.tent_chest",
                "tooltip.explorertent.explorer_tent",
                "message.explorertent.tent_placed",
                "message.explorertent.tent_packed",
                "message.explorertent.tent_no_space",
                "message.explorertent.tent_uneven_ground",
                "message.explorertent.tent_sleeping");

        for (String key : requiredKeys) {
            assertTrue(enUs.containsKey(key), "en_us.json must contain key: " + key);
            assertTrue(deDe.containsKey(key), "de_de.json must contain key: " + key);
            assertFalse(enUs.get(key).isBlank(), "en_us.json value for " + key + " must not be blank");
            assertFalse(deDe.get(key).isBlank(), "de_de.json value for " + key + " must not be blank");
        }

        assertEquals(
                enUs.keySet(), deDe.keySet(), "Both language files should have the exact same set of translation keys");
    }
}
