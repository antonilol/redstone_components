package com.antonilol.redstone_components;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.TntEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.TntEntity;

@Environment(EnvType.CLIENT)
public class MegaTntEntityRenderer extends TntEntityRenderer {
	
	public MegaTntEntityRenderer(Context context) {
		super(context);
	}

	@Override
	public void render(TntEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		matrices.scale(2, 2, 2);
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
		matrices.pop();
	}
}
