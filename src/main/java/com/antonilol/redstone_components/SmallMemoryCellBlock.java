package com.antonilol.redstone_components;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmallMemoryCellBlock extends MemoryCellBlock {

	protected SmallMemoryCellBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new SmallMemoryCellBlockEntity(pos, state);
	}

	@Override
	protected int getByteAddress(World world, BlockPos pos, BlockState state) {
		return getRightPower(world, pos, state);
	}

	@Override
	protected Mode getMode(World world, BlockPos pos, BlockState state) {
		if (getLeftPower(world, pos, state) > 0) {
			return Mode.WRITE;
		}
		return Mode.READ;
	}
}
