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

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class MemoryCellBlock extends AbstractRedstoneGateBlock implements BlockEntityProvider {

	public static enum Mode implements StringIdentifiable {
		READ("read"),
		WRITE("write");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return this.name;
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	public static final EnumProperty<Mode> MODE = EnumProperty.of("mode", Mode.class);

	protected int address;

	public static final String NAME = "memory_cell";

	protected MemoryCellBlock(Settings settings) {
		super(settings);

		setDefaultState(
			stateManager.getDefaultState()
			.with(FACING, Direction.NORTH)
			.with(MODE, Mode.READ)
		);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING, MODE);
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MemoryCellBlockEntity(pos, state);
	}

	protected int getBottomPower(World world, BlockPos pos, BlockState state) {
		return getPower(world, pos.offset(state.get(FACING).getOpposite()).offset(Direction.DOWN), state);
	}

	protected int getByteAddress(World world, BlockPos pos, BlockState state) {
		return (getLeftPower(world, pos, state) << 4) | getRightPower(world, pos, state);
	}

	protected int getLeftPower(World world, BlockPos pos, BlockState state) {
		return getPower(world, pos, state.with(FACING, state.get(FACING).rotateYClockwise()));
	}

	protected Mode getMode(World world, BlockPos pos, BlockState state) {
		if (getBottomPower(world, pos, state) > 0) {
			return Mode.WRITE;
		}
		return Mode.READ;
	}

	@Override
	protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
		BlockEntity be = world.getBlockEntity(pos);
		if (!(be instanceof MemoryCellBlockEntity)) {
			return 0;
		}
		final int d = ((MemoryCellBlockEntity) be).read(address);
		return d;
	}

	protected int getRightPower(World world, BlockPos pos, BlockState state) {
		return getPower(world, pos, state.with(FACING, state.get(FACING).rotateYCounterclockwise()));
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state) {
		return 2;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction dir) {
		return state.get(FACING) == dir ? getOutputLevel(world, pos, state) : 0;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		super.onBlockAdded(state, world, pos, oldState, notify);

		world.createAndScheduleBlockTick(pos, this, getUpdateDelayInternal(state));
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);

		world.createAndScheduleBlockTick(pos, this, getUpdateDelayInternal(state));
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		updateTarget(world, pos, state);
	}

	@Override
	protected void updatePowered(World world, BlockPos pos, BlockState state) {
		address = getByteAddress(world, pos, state);

		Mode mode = getMode(world, pos, state);

		if (mode != state.get(MODE)) {
			world.setBlockState(pos, state.with(MODE, mode), Block.NOTIFY_LISTENERS);
		}

		if (mode == Mode.WRITE) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof MemoryCellBlockEntity) {
				((MemoryCellBlockEntity) be).write(address, getPower(world, pos, state));
			}
		}

		world.createAndScheduleBlockTick(pos, this, getUpdateDelayInternal(state));
	}
}
