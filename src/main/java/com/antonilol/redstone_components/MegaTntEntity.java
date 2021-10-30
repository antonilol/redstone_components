package com.antonilol.redstone_components;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion.DestructionType;;

public class MegaTntEntity extends TntEntity {
	
	public static final int DEFAULT_FUSE = 100;
	
	public static MegaTntEntity create(EntityType<MegaTntEntity> type, World world) {
		return new MegaTntEntity(type, world);
	}
	
	private @Nullable LivingEntity igniter;
	
	public MegaTntEntity(EntityType<? extends MegaTntEntity> entityType, World world) {
		super(entityType, world);
	}
	
	public MegaTntEntity(World world, @Nullable LivingEntity igniter) {
		this(Main.MEGA_TNT_ENTITY, world);
		
		this.igniter = igniter;
	}
	
	@Override
	public @Nullable LivingEntity getCausingEntity() {
		return igniter;
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
