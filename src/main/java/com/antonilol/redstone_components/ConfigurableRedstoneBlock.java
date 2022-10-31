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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ConfigurableRedstoneBlock extends RedstoneBlock {

	private static BlockPos lastClickPos = null;
	private static World lastClickWorld = null;

	public static final BooleanProperty LOCKED = Properties.LOCKED;
	public static final IntProperty POWER = Properties.POWER;

	public static void setLockedLast(boolean locked) {
		if (lastClickPos != null && lastClickWorld != null) {
			lastClickWorld.setBlockState(lastClickPos, lastClickWorld.getBlockState(lastClickPos).with(LOCKED, locked));
		}
	}

	public static final String NAME = "configurable_redstone_block";

	protected ConfigurableRedstoneBlock(Settings settings) {
		super(settings);

		setDefaultState(
			stateManager.getDefaultState()
				.with(LOCKED, true)
				.with(POWER, 15));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(LOCKED, POWER);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWER);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
		BlockHitResult hit) {
		boolean locked = state.get(LOCKED);

		lastClickPos = pos;
		lastClickWorld = world;

		if (locked) {
			player.sendMessage(Text.of("This block is locked. Unlock it with /unlock or a debug stick"), true);
		} else if (player.getAbilities().allowModifyWorld) {
			world.setBlockState(pos, state.cycle(POWER));
			return ActionResult.success(world.isClient);
		}

		return ActionResult.PASS;
	}
}
