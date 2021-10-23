package com.antonilol.redstone_components;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;

public class ConfigurableTntEntity extends TntEntity {
	
	public ConfigurableTntEntity(EntityType<? extends TntEntity> entityType, World world) {
		super(entityType, world);
	}

	public void explode() {
		
	}
}
