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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class CurvedRepeaterBlock extends RepeaterBlock {

	public static enum Output implements StringIdentifiable {
		LEFT("left"),
		RIGHT("right");

		private final String name;

		private Output(String name) {
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

	public static final String NAME = "curved_repeater";

	public static final EnumProperty<Output> OUTPUT = EnumProperty.of("output", Output.class);

	protected CurvedRepeaterBlock(Settings settings) {
		super(settings);

		setDefaultState(
			getDefaultState()
			.with(OUTPUT, Output.RIGHT)
		);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);

		builder.add(OUTPUT);
	}

	@Override
	protected int getMaxInputLevelSides(WorldView world, BlockPos pos, BlockState state) {
		Direction side1 = state.get(FACING).getOpposite();
		Direction side2 = getOutput(state);
		return Math.max(getInputLevel(world, pos.offset(side1), side1), getInputLevel(world, pos.offset(side2), side2));
	}

	public Direction getOutput(BlockState state) {
		if (state.get(OUTPUT) == Output.RIGHT) {
			return state.get(FACING).rotateYClockwise();
		}
		return state.get(FACING).rotateYCounterclockwise();
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return state.with(LOCKED, isLocked(world, pos, state));
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) && getOutput(state) == direction ? 15 : 0;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return super.onUse(state.get(DELAY) == 4 ? state.cycle(OUTPUT) : state, world, pos, player, hand, hit);
	}

	@Override
	protected void updateTarget(World world, BlockPos pos, BlockState state) {
		BlockPos outputPos = pos.offset(getOutput(state.with(FACING, state.get(FACING).getOpposite())));
		world.updateNeighbor(outputPos, this, pos);
		world.updateNeighborsExcept(outputPos, this, getOutput(state));
	}
}
