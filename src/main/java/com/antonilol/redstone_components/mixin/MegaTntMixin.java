package com.antonilol.redstone_components.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.antonilol.redstone_components.MegaTntBlock;

import net.minecraft.block.Block;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(TntBlock.class)
public class MegaTntMixin {
	
	@Inject(
		at = @At("HEAD"),
		method = "Lnet/minecraft/block/TntBlock;primeTnt(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/LivingEntity;)V",
		cancellable = true
	)
	private static void primeMegaTnt(World world, BlockPos pos, @Nullable LivingEntity igniter, CallbackInfo info) {
		Block block = world.getBlockState(pos).getBlock();
		if (block instanceof MegaTntBlock) {
			if (!world.isClient) {
				((MegaTntBlock) block).primeMegaTnt(world, pos, igniter);
			}
			
			info.cancel();
		}
	}
}
