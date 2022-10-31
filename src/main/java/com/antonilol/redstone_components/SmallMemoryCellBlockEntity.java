package com.antonilol.redstone_components;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class SmallMemoryCellBlockEntity extends MemoryCellBlockEntity {

	public SmallMemoryCellBlockEntity(BlockPos pos, BlockState state) {
		super(Main.SMALL_MEMORY_CELL_BLOCK_ENTITY, pos, state, 16);
	}

	@Environment(EnvType.CLIENT)
	public static class Renderer implements BlockEntityRenderer<SmallMemoryCellBlockEntity> {

		public Renderer(BlockEntityRendererFactory.Context ctx) {
		}

		@Override
		public void render(SmallMemoryCellBlockEntity be, float tickDelta, MatrixStack matrixStack,
			VertexConsumerProvider vcp, int light, int overlay) {
			MemoryCellBlockEntity.Renderer.render(be, tickDelta, matrixStack, vcp, light, overlay, 4);
		}
	}
}
