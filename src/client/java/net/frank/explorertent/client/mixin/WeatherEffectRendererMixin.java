package net.frank.explorertent.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frank.explorertent.tent.ModTentBlocks;
import net.frank.explorertent.tent.TentCanvasBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Regen- und Schneesäulen enden an der Heightmap, also an der Oberkante des obersten Zeltblocks. Der First ragt
 * darüber hinaus; ohne Anhebung fällt der Niederschlag sichtbar in die Dachspitze.
 */
@Mixin(WeatherEffectRenderer.class)
abstract class WeatherEffectRendererMixin {

    @WrapOperation(
            method = "extractRenderState",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/client/multiplayer/ClientLevel;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"))
    private int explorertent$endRainAboveTentRidge(
            ClientLevel level, Heightmap.Types type, int x, int z, Operation<Integer> original) {
        int height = original.call(level, type, x, z);
        BlockState top = level.getBlockState(new BlockPos(x, height - 1, z));
        if (!top.is(ModTentBlocks.TENT_CANVAS)) return height;
        return height + TentCanvasBlock.rainFloorLift(top.getValue(TentCanvasBlock.PART));
    }
}
