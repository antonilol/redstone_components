/*
 * Copyright (c) 2021 Antoni Spaanderman
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

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
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
		return
			 state.get(REL_X) |
			(state.get(REL_Y) << 1) |
			(state.get(REL_Z) << 2);
	}

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

	public void breakBlocks(World world, BlockPos pos, BlockState state, @Nullable PlayerEntity player) {
		BlockPos origin = getOrigin(pos, state);

		for (int i = 0; i < 8; i++) {
			int x =  i & 0b001;
			int y = (i & 0b010) >> 1;
			int z = (i & 0b100) >> 2;

			BlockPos offset = origin
				.offset(Axis.X, x)
				.offset(Axis.Y, y)
				.offset(Axis.Z, z);

			BlockState blockState = world.getBlockState(offset);
			if (blockState.isOf(this)) {
				if (player == null) {
					world.removeBlock(offset, false);
				} else {
					world.setBlockState(offset, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
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

			if (!world.getBlockState(
					pos
						.offset(dirX, x)
						.offset(dirY, y)
						.offset(dirZ, z)
				).canReplace(ctx)) {
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
		breakBlocks(world, pos, state, player);

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

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);

		BlockPos origin = getOrigin(pos, state);

		for (int i = 0; i < 8; i++) {
			if (i == getRelIntPos(state)) {
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
		}
	}

	public @Nullable MegaTntEntity primeMegaTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
		BlockState state = world.getBlockState(pos);

		if (!state.isOf(this)) {
			if (lastReplacedState == null) {
				return null;
			}

			state = lastReplacedState;

			if (!state.isOf(this)) {
				return null;
			}
		}

		BlockPos origin = getOrigin(pos, state);
		Vec3d spawnPos = Vec3d.of(origin.add(1, 0, 1));

		MegaTntEntity tnt = new MegaTntEntity(world, igniter);
		tnt.setPosition(spawnPos);
		tnt.prevX = spawnPos.getX();
		tnt.prevY = spawnPos.getY();
		tnt.prevZ = spawnPos.getZ();
		tnt.setFuse(MegaTntEntity.DEFAULT_FUSE);
		world.spawnEntity(tnt);

		breakBlocks(world, pos, state, null);

		world.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		world.emitGameEvent(igniter, GameEvent.PRIME_FUSE, pos);

		return tnt;
	}
}
