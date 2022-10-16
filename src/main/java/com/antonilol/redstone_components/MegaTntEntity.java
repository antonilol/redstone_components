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

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
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

	public MegaTntEntity(World world, Vec3d pos, @Nullable LivingEntity igniter) {
		this(Main.MEGA_TNT_ENTITY, world);

		this.igniter = igniter;

		setPosition(pos);
		prevX = pos.getX();
		prevY = pos.getY();
		prevZ = pos.getZ();
		setFuse(MegaTntEntity.DEFAULT_FUSE);
		double angle = world.random.nextDouble() * Math.PI * 2;
		setVelocity(Math.cos(angle) * 0.02, 0.2, Math.sin(angle) * 0.02);
	}

	public void ignite() {
		world.spawnEntity(this);
		world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
		world.emitGameEvent(null, GameEvent.ENTITY_PLACE, getPos());
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
