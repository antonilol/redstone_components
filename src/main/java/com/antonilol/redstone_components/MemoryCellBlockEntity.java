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

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class MemoryCellBlockEntity extends BlockEntity {

	public static final String MEMORY_TAG_NAME = "memory";

	public final int memorySize;
	public final int displayHeight;
	private byte[] memory;

	protected MemoryCellBlockEntity(BlockEntityType<? extends MemoryCellBlockEntity> type, BlockPos pos,
		BlockState state, int memorySize) {
		super(type, pos, state);

		displayHeight = 1 << (int) (Math.log(memorySize) / Math.log(2) / 2);
		if (memorySize != displayHeight * displayHeight) {
			throw new RuntimeException("Invalid memorySize " + memorySize + ", memorySize must be an even power of 2");
		}

		this.memorySize = memorySize;
		memory = new byte[memorySize / 2];
	}

	public MemoryCellBlockEntity(BlockPos pos, BlockState state) {
		this(Main.MEMORY_CELL_BLOCK_ENTITY, pos, state, 256);
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
			memory[index] = (byte) (b & 0x0f | value << 4);
		} else {
			memory[index] = (byte) (b & 0xf0 | value);
		}

		markDirty();
		world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
	}

	@Override
	public void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);

		tag.putByteArray(MEMORY_TAG_NAME, memory);
	}

	@Override
	public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}

	@Environment(EnvType.CLIENT)
	public static class Renderer implements BlockEntityRenderer<MemoryCellBlockEntity> {

		public Renderer(BlockEntityRendererFactory.Context ctx) {
		}

		private static final Identifier TEXTURE = new Identifier(Main.MOD_ID, "textures/block/memory_cell_glow.png");

		@Override
		public void render(MemoryCellBlockEntity be, float tickDelta, MatrixStack matrixStack,
			VertexConsumerProvider vcp, int light, int overlay) {
			render(be, tickDelta, matrixStack, vcp, light, overlay, 8);
		}

		protected static void render(MemoryCellBlockEntity be, float tickDelta, MatrixStack matrixStack,
			VertexConsumerProvider vcp, int light, int overlay, int renderedDisplayHeight) {
			BlockState state = be.world.getBlockState(be.getPos());
			if (!(state.getBlock() instanceof MemoryCellBlock)) {
				return;
			}

			float rot;
			switch (state.get(MemoryCellBlock.FACING)) {
			case SOUTH:
				rot = 0;
				break;
			case EAST:
				rot = 90;
				break;
			case NORTH:
				rot = 180;
				break;
			case WEST:
				rot = 270;
				break;
			default:
				return;
			}

			matrixStack.push();
			matrixStack.translate(0.5d, 0.0d, 0.5d);
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rot));

			MatrixStack.Entry entry = matrixStack.peek();
			Matrix4f pos = entry.getPositionMatrix();
			Matrix3f normal = entry.getNormalMatrix();
			VertexConsumer vc = vcp.getBuffer(RenderLayer.getBeaconBeam(TEXTURE, true));

			float scale = 1f / 16f * renderedDisplayHeight / be.displayHeight;
			int offset = be.displayHeight / 2;

			for (int z = 0; z < be.displayHeight; z++) {
				for (int x = 0; x < be.displayHeight; x++) {
					int b = be.read(be.displayHeight * z + x);
					if (b != 0) {
						face(pos, normal, vc, (b + 8) * 11, x - offset, z - offset, scale,
							LightmapTextureManager.MAX_LIGHT_COORDINATE);
					}
				}
			}

			matrixStack.pop();
		}

		private static void face(Matrix4f pos, Matrix3f normal, VertexConsumer vc, int glow, float x, float z,
			float scale, int light) {
			vertex(pos, normal, vc, glow, x, z, scale, light);
			vertex(pos, normal, vc, glow, x, z + 1, scale, light);
			vertex(pos, normal, vc, glow, x + 1, z + 1, scale, light);
			vertex(pos, normal, vc, glow, x + 1, z, scale, light);
		}

		private static void vertex(Matrix4f pos, Matrix3f normal, VertexConsumer vc, int glow, float x, float z,
			float scale, int light) {
			vc.vertex(pos, x * scale, 0.12501f, z * scale);
			vc.color(0, 255, 0, glow);
			vc.texture(x, z);
			vc.overlay(OverlayTexture.DEFAULT_UV);
			vc.light(light);
			vc.normal(normal, 0.0f, 1.0f, 0.0f);
			vc.next();
		}
	}
}
