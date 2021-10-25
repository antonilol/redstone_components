package com.antonilol.redstone_components;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {
	
	@Override
	public void onInitializeClient() {
        EntityRendererRegistry.register(Main.MEGA_TNT_ENTITY, (context) -> {
            return new MegaTntEntityRenderer(context);
        });
 	}
}
