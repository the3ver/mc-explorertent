package net.frank.explorertent.tent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TentRainColumnTest {

    @BeforeAll
    static void bootstrap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
    }

    // Vanilla zeichnet Regen- und Schneesäulen bis zur MOTION_BLOCKING-Heightmap, also bis zur Oberkante des
    // obersten Zeltblocks (32 px über dem Zeltboden). Der steile First ragt bis 40 px darüber hinaus: ohne Anhebung
    // fällt der Regen in der Mittelreihe sichtbar in die Dachspitze.
    @Test
    void rainColumnsEndAboveTheVisibleRoof() {
        double blockTop = ExplorerTentLogic.HEIGHT * 16.0;
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            if (!part.name().startsWith("ROOF_")) continue;
            double rainFloor = blockTop + 16.0 * TentCanvasBlock.rainFloorLift(part);
            double roofTop =
                    TentCanvasBlock.isRidgeCell(part) ? TentRoofLogic.RIDGE_HEIGHT : TentRoofLogic.roofTopAt(16.0);
            assertTrue(rainFloor >= roofTop, part + ": Regen endet bei " + rainFloor + " px, Dach bei " + roofTop);
        }
    }

    @Test
    void wallsAndLowRoofCellsKeepTheVanillaRainFloor() {
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            if (TentCanvasBlock.isRidgeCell(part)) continue;
            assertEquals(0, TentCanvasBlock.rainFloorLift(part), part.name());
        }
    }
}
