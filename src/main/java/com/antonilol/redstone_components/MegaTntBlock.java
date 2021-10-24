package com.antonilol.redstone_components;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;

public class MegaTntBlock extends TntBlock {
	
	public static final IntProperty REL_X = IntProperty.of("rel_x", 0, 1);
	public static final IntProperty REL_Y = IntProperty.of("rel_y", 0, 1);
	public static final IntProperty REL_Z = IntProperty.of("rel_z", 0, 1);

	public MegaTntBlock(Settings settings) {
		super(settings);
		
		setDefaultState(
			stateManager.getDefaultState() // BedBlock
			.with(UNSTABLE, false)
			.with(REL_X, 0)
			.with(REL_Y, 0)
			.with(REL_Z, 0)
		);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(REL_X, REL_Y, REL_Z);
	}
	
	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		@Nullable BlockState state;
		
		for (int i = 0; i < 8; i++) {
			state = getPlacementState(ctx, i);
			
			if (state != null) {				
				return state;
			}
		}
		
		return null;
	}
	
	private @Nullable BlockState getPlacementState(ItemPlacementContext ctx, int invert) {
		Direction dirX = null, dirY = null, dirZ = null;
		
		for (Direction d : ctx.getPlacementDirections()) {
			if (d.getAxis() == Axis.X && dirX == null) {
				if ((invert & 0b010) == 0) {
					dirX = d;
				} else {
					dirX = d.getOpposite();
				}
			} else if (d.getAxis() == Axis.Y && dirY == null) {
				if ((invert & 0b001) == 0) {
					dirY = d;
				} else {
					dirY = d.getOpposite();
				}
			} else if (d.getAxis() == Axis.Z && dirZ == null) {
				if ((invert & 0b100) == 0) {
					dirZ = d;
				} else {
					dirZ = d.getOpposite();
				}
			} else {
				break;
			}
		}
		
		BlockPos pos = ctx.getBlockPos();
		World world = ctx.getWorld();
		
		for (int i = 1; i < 8; i++) {
			int x =  i & 0b001;
			int y = (i & 0b010) >> 1;
			int z = (i & 0b100) >> 2;
			
			if (!(
				world.getBlockState(pos.offset(dirX, x)).canReplace(ctx) &&
				world.getBlockState(pos.offset(dirY, y)).canReplace(ctx) &&
				world.getBlockState(pos.offset(dirZ, z)).canReplace(ctx)
			)) {
				return null;
			}
		}
		
		return getDefaultState()
			.with(REL_X, dirX.getOffsetX() == 1 ? 0 : 1)
			.with(REL_Y, dirY.getOffsetY() == 1 ? 0 : 1)
			.with(REL_Z, dirZ.getOffsetZ() == 1 ? 0 : 1);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		
		int relX = state.get(REL_X);
		int relY = state.get(REL_Y);
		int relZ = state.get(REL_Z);
		int mask = relX | (relY << 1) | (relZ << 2);
				
		BlockPos origin = pos
			.offset(Axis.X, -relX)
			.offset(Axis.Y, -relY)
			.offset(Axis.Z, -relZ);
				
		if (!world.isClient) {
			for (int i = 0; i < 8; i++) {
				if (i == mask) {
					continue;
				}
				
				int x =  i & 0b001;
				int y = (i & 0b010) >> 1;
				int z = (i & 0b100) >> 2;
				
				BlockPos offset = origin
					.offset(Axis.X, x)
					.offset(Axis.Y, y)
					.offset(Axis.Z, z);
								
				world.setBlockState(
					offset,
					state
					.with(REL_X, x)
					.with(REL_Y, y)
					.with(REL_Z, z),
					Block.NOTIFY_ALL
				);
				world.updateNeighbors(offset, Blocks.AIR);
				state.updateNeighbors(world, offset, Block.NOTIFY_ALL);
			}
		}
	}
}
