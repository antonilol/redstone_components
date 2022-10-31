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

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RealTimeClock extends AbstractRedstoneGateBlock {

	public static enum Mode implements StringIdentifiable {
		SECONDS("seconds"),
		MINUTES("minutes"),
		HOURS("hours"),
		DAYS("days"),
		MONTHS("months"),
		YEARS("years");

		private final String name;

		private Mode(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public static final EnumProperty<Mode> MODE = EnumProperty.of("mode", Mode.class);

	protected int address;

	public static final String NAME = "real_time_clock";

	protected RealTimeClock(Settings settings) {
		super(settings);

		setDefaultState(
			stateManager.getDefaultState()
				.with(FACING, Direction.NORTH)
				.with(MODE, Mode.SECONDS));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING, MODE);
	}

	protected int getTime(BlockState state) {
		ZonedDateTime time = Instant.now().atZone(ZoneOffset.UTC);
		switch (state.get(MODE)) {
		case SECONDS:
			return time.getSecond();
		case MINUTES:
			return time.getMinute();
		case HOURS:
			return time.getHour();
		case DAYS:
			return time.getDayOfMonth();
		case MONTHS:
			return time.getMonthValue();
		case YEARS:
			return time.getYear() - 1970;
		}
		return 0;
	}

	@Override
	protected int getOutputLevel(BlockView world, BlockPos pos, BlockState state) {
		return getTime(state) & 0xf;
	}

	protected int getSidePowerLevel(BlockView world, BlockPos pos, BlockState state) {
		return getTime(state) >> 4 & 0xf;
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state) {
		return 2;
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction dir) {
		Direction facing = state.get(FACING);
		if (facing == dir) {
			return getOutputLevel(world, pos, state);
		} else if (facing.getOpposite() != dir) {
			return getSidePowerLevel(world, pos, state);
		}
		return 0;
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
		world.createAndScheduleBlockTick(pos, this, getUpdateDelayInternal(state));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
		BlockHitResult hit) {
		if (player.getAbilities().allowModifyWorld) {
			world.setBlockState(pos, state.cycle(MODE));
			return ActionResult.success(world.isClient);
		}

		return ActionResult.PASS;
	}
}
