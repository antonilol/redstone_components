/*
 * Copyright (c) 2021 - 2022 Antoni Spaanderman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.antonilol.redstone_components;

import java.util.HashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.explosion.Explosion;

public class MegaTntBlock extends TntBlock {

	public static BlockState lastReplacedState = null;

	public static final IntProperty REL_X = IntProperty.of("relative_x", 0, 1);
	public static final IntProperty REL_Y = IntProperty.of("relative_y", 0, 1);
	public static final IntProperty REL_Z = IntProperty.of("relative_z", 0, 1);

	private static BlockPos getOrigin(BlockPos pos, BlockState state) {
		return pos
			.offset(Axis.X, -state.get(REL_X))
			.offset(Axis.Y, -state.get(REL_Y))
			.offset(Axis.Z, -state.get(REL_Z));
	}

	private static int getRelIntPos(BlockState state) {
		return state.get(REL_X) |
			state.get(REL_Y) << 1 |
			state.get(REL_Z) << 2;
	}

	public static final String NAME = "mega_tnt";

	protected MegaTntBlock(Settings settings) {
		super(settings);

		setDefaultState(
			getDefaultState()
				.with(REL_X, 0)
				.with(REL_Y, 0)
				.with(REL_Z, 0));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(REL_X, REL_Y, REL_Z);
	}

	private void breakMegaTnt(World world, BlockPos origin, @Nullable PlayerEntity player) {
		for (int i = 0; i < 8; i++) {
			int x = i & 0b001;
			int y = (i & 0b010) >> 1;
			int z = (i & 0b100) >> 2;

			BlockPos offset = origin
				.offset(Axis.X, x)
				.offset(Axis.Y, y)
				.offset(Axis.Z, z);

			BlockState blockState = world.getBlockState(offset);
			if (blockState.isOf(this)) {
				world.setBlockState(offset, Blocks.AIR.getDefaultState());
				if (player != null) {
					world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, offset, Block.getRawIdFromState(blockState));
				}
			}
		}

		if (player != null && !player.isCreative()) {
			Block.dropStacks(getDefaultState(), world, origin, null, player, new ItemStack(this));
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.BLOCK;
	}

	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		@Nullable
		BlockState state;

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
			int x = i & 0b001;
			int y = (i & 0b010) >> 1;
			int z = (i & 0b100) >> 2;

			if (!world.getBlockState(
				pos
					.offset(dirX, x)
					.offset(dirY, y)
					.offset(dirZ, z))
				.canReplace(ctx)) {
				return null;
			}
		}

		return getDefaultState()
			.with(REL_X, dirX.getOffsetX() == 1 ? 0 : 1)
			.with(REL_Y, dirY.getOffsetY() == 1 ? 0 : 1)
			.with(REL_Z, dirZ.getOffsetZ() == 1 ? 0 : 1);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		breakMegaTnt(world, getOrigin(pos, state), player);

		super.onBreak(world, pos, state, player);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		if (!world.isClient) {
			MegaTntEntity tnt = primeMegaTnt(world, pos, explosion.getCausingEntity());
			if (tnt != null) {
				tnt.setFuse(MegaTntEntity.DEFAULT_FUSE / 2);
			}
		}
	}

	private HashMap<BlockPos, Boolean> primed = new HashMap<BlockPos, Boolean>();

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
		ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);

		BlockPos origin = getOrigin(pos, state);

		primed.put(origin, false);

		for (int i = 0; i < 8; i++) {
			if (i == getRelIntPos(state)) {
				continue;
			}

			int x = i & 0b001;
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
					.with(REL_Z, z));
		}

		if (primed.get(origin)) {
			primeMegaTnt(world, origin, null, true);
		}

		primed.remove(origin);
	}

	public @Nullable MegaTntEntity primeMegaTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
		return primeMegaTnt(world, pos, igniter, false);
	}

	private @Nullable MegaTntEntity primeMegaTnt(World world, BlockPos pos, @Nullable LivingEntity igniter,
		boolean force) {
		BlockPos origin;

		if (!force) {
			BlockState state = world.getBlockState(pos);

			if (!state.isOf(this)) {
				state = lastReplacedState;
				lastReplacedState = null;

				if (state == null || !state.isOf(this)) {
					return null;
				}
			}

			origin = getOrigin(pos, state);

			if (primed.get(origin) != null) {
				primed.put(origin, true);
				return null;
			}
		} else {
			origin = pos;
		}

		breakMegaTnt(world, origin, null);

		MegaTntEntity tnt = new MegaTntEntity(world, Vec3d.of(origin.add(1, 0, 1)), igniter);

		tnt.ignite();

		return tnt;
	}

	public static class DispenserBehavior extends ItemDispenserBehavior {

		@Override
		protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			Vec3d pos = Vec3d.of(pointer.getPos())
				.add(Vec3d.of(Vec3i.ZERO.offset(pointer.getBlockState().get(DispenserBlock.FACING))).multiply(1.5d))
				.add(0.5d, -0.5d, 0.5d);
			new MegaTntEntity(pointer.getWorld(), pos, null).ignite();
			stack.decrement(1);
			return stack;
		}
	}
}
