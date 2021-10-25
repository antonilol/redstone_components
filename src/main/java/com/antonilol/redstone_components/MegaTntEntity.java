package com.antonilol.redstone_components;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;;

public class MegaTntEntity extends TntEntity {
	
	public MegaTntEntity(EntityType<? extends MegaTntEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@Override
	public void tick() {
		boolean explode = getFuse() <= 1;
		
		if (explode) {
			// Prevent default explosion
			setFuse(2);
		}
		
		super.tick();
		
		if (explode) {
			discard();
			if (!world.isClient) {
				world.createExplosion(this, getX(), getY(), getZ(), 40, DestructionType.BREAK);
			}
		}
	}
}
