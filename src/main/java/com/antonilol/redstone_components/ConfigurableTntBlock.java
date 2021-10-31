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
import net.minecraft.block.TntBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConfigurableTntBlock extends TntBlock {
	
	public static enum ExplosionPower implements StringIdentifiable {
		_00( "0",   0),
		_01( "1",   1),
		_02( "2",   2),
		_03( "3",   3),
		_04( "4",   4),
		_05( "5",   5),
		_06( "6",   6),
		_07( "7",   7),
		_08( "8",   8),
		_09( "9",   9),
		_10("10",  10),
		_11("11",  11),
		_12("12",  12),
		_13("13",  13),
		_14("14",  14),
		_15("15",  15),
		NBT("nbt", -1);
		
		public static final ExplosionPower DEFAULT = _04;

		public static ExplosionPower fromNumber(int power) {
			if (power < 0) {
				return _00;
			}
			for (ExplosionPower p : values()) {
				if (p.power == power) {
					return p;
				}
			}
			return NBT;
		}
		
		private final String name;
		private final int power;
		
		private ExplosionPower(String name, int power) {
			this.name = name;
			this.power = power;
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
	
	public static final EnumProperty<ExplosionPower> EXPLOSION_POWER = EnumProperty.of("explosion_power", ExplosionPower.class);
	
	public ConfigurableTntBlock(Settings settings) {
		super(settings);
		
		setDefaultState(
			stateManager.getDefaultState()
			.with(UNSTABLE, false)
			.with(EXPLOSION_POWER, ExplosionPower.DEFAULT)
		);
	}
	
	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(EXPLOSION_POWER);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ActionResult result = super.onUse(state, world, pos, player, hand, hit);
		
		return result;
	}
}
