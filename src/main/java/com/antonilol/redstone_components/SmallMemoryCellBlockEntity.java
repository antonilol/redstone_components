package com.antonilol.redstone_components;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class SmallMemoryCellBlockEntity extends MemoryCellBlockEntity {

	public SmallMemoryCellBlockEntity(BlockPos pos, BlockState state) {
		super(Main.SMALL_MEMORY_CELL_BLOCK_ENTITY, pos, state, 8);
	}
}
