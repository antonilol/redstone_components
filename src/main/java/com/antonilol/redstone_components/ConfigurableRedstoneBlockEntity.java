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

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

@Deprecated(forRemoval=true)
public class ConfigurableRedstoneBlockEntity extends BlockEntity {

	public static final String LOCKED_TAG_NAME = "locked";
	
	public static final String POWER_TAG_NAME = "power";
	
	private boolean locked = true;
	
	private int power = 15;
	
	private boolean initialized = false;
	
	public ConfigurableRedstoneBlockEntity(BlockPos pos, BlockState state) {
		super(Main.CONFIGURABLE_REDSTONE_BLOCK_ENTITY, pos, state);
	}
	
	public int cyclePower() {
		initialized = true;
		
		power++;
		
		if (power > 15) {
			power = 0;
		}
		
		markDirty();

		return power;
	}
	
	public int getPower() {
		return power;
	}

	public boolean isLocked() {
		return locked;
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		
		initialized = true;
		
		System.out.println("readNbt");
		setPower(tag.getInt(POWER_TAG_NAME));
		locked = tag.getBoolean(LOCKED_TAG_NAME);
		System.out.println("        " + power + locked);
	}

	public void setLocked(boolean locked) {
		initialized = true;
		
		this.locked = locked;
		
		markDirty();
	}
	
	public void setPower(int power) {
		initialized = true;
		
		if (power < 0) {
			this.power = 0;
		} else if (power > 15) {
			this.power = 15;
		} else {
			this.power = power;
		}
		
		markDirty();
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		super.writeNbt(tag);

		System.out.println("writeNbt");
		if (initialized) {
			tag.putInt(POWER_TAG_NAME, power);
			tag.putBoolean(LOCKED_TAG_NAME, locked);
			System.out.println("         " + power + locked);
		}
		
		return tag;
	}
}
