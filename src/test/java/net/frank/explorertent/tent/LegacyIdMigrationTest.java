package net.frank.explorertent.tent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import net.frank.explorertent.ExplorerTentMod;
import net.minecraft.SharedConstants;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Bis WorldExplorer 0.12 lag das Zelt unter mc3ver:*. Welten und Inventare aus dieser Zeit müssen mit dem eigenständigen
 * Mod weiter laden: aufgestellte Zelte, gepackte Zelte, Truhe und Ofen als Block-Entity. Den Inhalt gepackter Zelte
 * trägt minecraft:container, der vom Namespace unabhängig ist.
 */
class LegacyIdMigrationTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        new ExplorerTentMod().onInitialize();
    }

    private static Identifier legacy(String path) {
        return Identifier.fromNamespaceAndPath("mc3ver", path);
    }

    @Test
    void legacyBlockIdsResolveToTentBlocks() {
        List<Block> blocks = List.of(
                ModTentBlocks.TENT_CANVAS,
                ModTentBlocks.TENT_POLE,
                ModTentBlocks.TENT_SLEEPING_BAG,
                ModTentBlocks.TENT_FURNACE,
                ModTentBlocks.TENT_FLAP,
                ModTentBlocks.TENT_CHEST);
        for (Block block : blocks) {
            String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
            assertSame(block, BuiltInRegistries.BLOCK.getValue(legacy(path)), path);
        }
    }

    // Chunk-Paletten speichern Blockzustände mit BlockState.CODEC
    @Test
    void legacyBlockStateFromChunkPaletteKeepsProperties() {
        BlockState original = ModTentBlocks.TENT_CHEST
                .defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, Direction.EAST);
        CompoundTag saved = (CompoundTag)
                BlockState.CODEC.encodeStart(NbtOps.INSTANCE, original).getOrThrow();
        CompoundTag legacySaved = saved.copy();
        legacySaved.putString(nameKey(saved), "mc3ver:tent_chest");

        BlockState loaded = BlockState.CODEC.parse(NbtOps.INSTANCE, legacySaved).getOrThrow();

        assertEquals(original, loaded);
    }

    @Test
    void legacyBlockEntityIdsResolveToTentBlockEntities() {
        assertSame(
                ModTentBlocks.TENT_CHEST_BLOCK_ENTITY,
                BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(legacy("tent_chest")));
        assertSame(
                ModTentBlocks.TENT_FURNACE_BLOCK_ENTITY,
                BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(legacy("tent_furnace")));
    }

    // ItemStack liest seine id mit diesem Codec
    @Test
    void legacyItemIdDecodesToExplorerTent() {
        Item item = BuiltInRegistries.ITEM
                .byNameCodec()
                .parse(NbtOps.INSTANCE, StringTag.valueOf("mc3ver:explorer_tent"))
                .getOrThrow();

        assertSame(ModTentItems.EXPLORER_TENT, item);
    }

    // Feldname der Block-ID im gespeicherten Zustand, unabhängig vom Speicherformat der Minecraft-Version
    private static String nameKey(CompoundTag saved) {
        for (String key : saved.keySet()) {
            Tag value = saved.get(key);
            if (value instanceof StringTag(String id) && id.equals("explorertent:tent_chest")) return key;
        }
        throw new AssertionError("Block-ID fehlt in " + saved);
    }
}
