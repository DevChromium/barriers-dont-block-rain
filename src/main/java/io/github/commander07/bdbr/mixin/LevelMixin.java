package io.github.commander07.bdbr.mixin;

import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(method = "getHeight", at = @At("HEAD"), cancellable = true)
    private void bdbr$getHeight(Heightmap.Types types, int i, int j, CallbackInfoReturnable<Integer> cir) {
        if (types != Heightmap.Types.MOTION_BLOCKING) return;

        Level level = (Level)(Object)this;

        int bottom = level.getMinY();
        int topExclusive = bottom + level.getHeight(); // exclusive upper bound

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(i, 0, j);

        for (int y = topExclusive - 1; y >= bottom; y--) {
            pos.set(i, y, j);
            BlockState state = level.getBlockState(pos);

            if ((state.blocksMotion() || !state.getFluidState().isEmpty())
                    && !(state.getBlock() instanceof BarrierBlock)) {
                cir.setReturnValue(y + 1);
                return;
            }
        }

        cir.setReturnValue(bottom);
    }
}
