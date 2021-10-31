package com.antonilol.redstone_components.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.antonilol.redstone_components.MegaTntBlock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(World.class)
public class WorldMixin {
	
	@Shadow
	private BlockState getBlockState(BlockPos pos) {
		return null;
	}

	@Inject(
		at = @At("HEAD"),
		method = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;ILorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V"
	)
	public void setBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> info) {
		MegaTntBlock.lastReplacedState = getBlockState(pos);
	}
}
