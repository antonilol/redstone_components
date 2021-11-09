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
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class MemoryCellBlockEntity extends BlockEntity {

	public static final String MEMORY_TAG_NAME = "memory";

	private byte[] memory;

	protected MemoryCellBlockEntity(BlockEntityType<? extends MemoryCellBlockEntity> type, BlockPos pos, BlockState state, int memorySize) {
		super(type, pos, state);

		memory = new byte[memorySize];
	}

	public MemoryCellBlockEntity(BlockPos pos, BlockState state) {
		this(Main.MEMORY_CELL_BLOCK_ENTITY, pos, state, 128);
	}

	public int read(int address) {
		final int b = memory[(address & 0xff) >> 1] & 0xff;
		if ((address & 1) == 0) {
			return b >> 4;
		}
		return b & 0xf;
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);

		byte[] m = tag.getByteArray(MEMORY_TAG_NAME);

		if (m.length == memory.length) {
			memory = m;
		}
	}

	public void write(int address, int value) {
		if (value < 0) {
			value = 0;
		} else if (value > 15) {
			value = 15;
		}

		final int index = (address & 0xff) >> 1;
		final byte b = memory[index];
		if ((address & 1) == 0) {
			memory[index] = (byte) ((b & 0x0f) | (value << 4));
		} else {
			memory[index] = (byte) ((b & 0xf0) |  value      );
		}

		markDirty();
	}

	@Override
	public NbtCompound writeNbt(NbtCompound tag) {
		super.writeNbt(tag);

		tag.putByteArray(MEMORY_TAG_NAME, memory);

		return tag;
	}
}
