package net.frank.explorertent.tent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TentRainSplashTest {

    // ClientLevel setzt Regenspritzer auf blockY + Kollisionsform.max(Y, dx, dz) (Kontext ohne Entity).
    // Ein Regenpartikel ragt bis ~3,2 Pixel um seinen Mittelpunkt: auf der nur 2 Pixel dicken Plane
    // scheint er nach innen durch. Deshalb muss jede Regen-Oberkante mindestens 4 Pixel über der Plane liegen.
    private static final double SPLASH_LIFT = 4.0 / 16.0;

    @Test
    void rainSplashesLandOnTopOfEveryRoofPart() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        java.util.List<String> leaks = new java.util.ArrayList<>();
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            if (!part.name().startsWith("ROOF_")) continue;
            for (net.minecraft.core.Direction facing : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                net.minecraft.world.phys.shapes.VoxelShape shape = TentCanvasBlock.getRainShape(part, facing);
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        double dx = (i + 0.5) / 16.0;
                        double dz = (j + 0.5) / 16.0;
                        // Spalten ohne Plane (hinter dem Giebel) würden -Infinity liefern: Vanilla setzt den Spritzer
                        // dann auf die Blockunterkante direkt an die Rückwand, von wo er nach innen durchscheint
                        double top = shape.max(net.minecraft.core.Direction.Axis.Y, dx, dz);
                        if (top < 1.0 + SPLASH_LIFT - 1e-9) {
                            leaks.add(part + "/" + facing + " bei " + i + "," + j + " -> " + top);
                        }
                    }
                }
            }
        }
        assertTrue(
                leaks.isEmpty(),
                leaks.size() + " Spalten ohne Dachoberkante, z. B. " + leaks.subList(0, Math.min(5, leaks.size())));
    }

    @Test
    void rainSplashesLandAboveTheRidge() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        // Der First ragt über die obere Mittelzelle hinaus; Spritzer darunter wären von innen zu sehen.
        // Blick nach Norden: die Mittelzelle reicht von x = 16 (Westrand) bis 32 im Zeltquerschnitt.
        for (TentCanvasBlock.CanvasPart part : java.util.List.of(
                TentCanvasBlock.CanvasPart.ROOF_RIDGE,
                TentCanvasBlock.CanvasPart.ROOF_FRONT,
                TentCanvasBlock.CanvasPart.ROOF_BACK)) {
            net.minecraft.world.phys.shapes.VoxelShape shape =
                    TentCanvasBlock.getRainShape(part, net.minecraft.core.Direction.NORTH);
            for (int i = 0; i < 16; i++) {
                double roof = Math.max(TentRoofLogic.roofTopAt(16 + i), TentRoofLogic.roofTopAt(17 + i)) - 16;
                // VoxelShape.max(Y, …) nimmt erst z, dann x
                double top = shape.max(net.minecraft.core.Direction.Axis.Y, 0.5, (i + 0.5) / 16.0);
                assertTrue(
                        top >= (roof + SPLASH_LIFT * 16) / 16.0 - 1e-9,
                        part + " Spalte " + i + ": Regenkante " + top * 16 + " px, Dach " + roof + " px");
            }
        }
    }

    @Test
    void entitiesKeepTheNormalRoofCollision() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        // Die Regenkappe gilt nur für Abfragen ohne Entity, Spieler und Mobs stoßen weiter an die echte Plane
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            for (net.minecraft.core.Direction facing : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                assertTrue(
                        TentCanvasBlock.getWallShape(part, facing).max(net.minecraft.core.Direction.Axis.Y) <= 1.0,
                        part + " darf für Entities nicht über den Block hinausragen");
            }
        }
    }

    @Test
    void fallingRainDropsCollideWithTheRainCap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        // Seit 26.3 fragen Partikel mit CollisionContext.positionContext(y) ab statt mit empty(). Ohne Kappe fallen
        // die Tropfen nach dem Spawnen auf die dünne Plane und scheinen nach innen durch.
        assertTrue(TentCanvasBlock.usesRainShape(net.minecraft.world.phys.shapes.CollisionContext.empty()));
        assertTrue(
                TentCanvasBlock.usesRainShape(net.minecraft.world.phys.shapes.CollisionContext.positionContext(70.0)));
    }

    @Test
    void rainDropsKeepClearOfTheSteepRoofInEveryDirection() {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();

        // Ein Tropfen ragt nicht nur nach unten, sondern auch seitlich ~3,2 Pixel über seinen Mittelpunkt. Am steilen
        // Dach steigt die Plane bergauf auf dieser Breite um fast 4 Pixel: die Kappe muss über dem höchsten Dachpunkt
        // im Umkreis liegen, nicht nur über dem Punkt direkt darunter.
        java.util.List<String> leaks = new java.util.ArrayList<>();
        for (TentCanvasBlock.CanvasPart part : TentCanvasBlock.CanvasPart.values()) {
            if (!part.name().startsWith("ROOF_")) continue;
            // Blick nach Norden: links = Westen. Linke Zellen beginnen bei x = 0, die Mitte bei 16, rechte bei 32.
            int cellStart = part.name().contains("LEFT") ? 0 : part.name().contains("RIGHT") ? 32 : 16;
            net.minecraft.world.phys.shapes.VoxelShape shape =
                    TentCanvasBlock.getRainShape(part, net.minecraft.core.Direction.NORTH);
            for (int i = 0; i < 16; i++) {
                double x = cellStart + i + 0.5;
                double highestRoof = 0;
                for (double r = -SPLASH_LIFT * 16; r <= SPLASH_LIFT * 16; r += 0.25) {
                    highestRoof = Math.max(highestRoof, TentRoofLogic.roofTopAt(Math.clamp(x + r, 0, 48)));
                }
                double top = 16 + shape.max(net.minecraft.core.Direction.Axis.Y, 0.5, (i + 0.5) / 16.0) * 16;
                if (top < highestRoof + SPLASH_LIFT * 16 - 1e-9) {
                    leaks.add(part + " Spalte " + i + ": Kappe " + top + " px, nötig "
                            + (highestRoof + SPLASH_LIFT * 16));
                }
            }
        }
        assertTrue(
                leaks.isEmpty(),
                leaks.size() + " Spalten zu knapp, z. B. " + leaks.subList(0, Math.min(5, leaks.size())));
    }
}
