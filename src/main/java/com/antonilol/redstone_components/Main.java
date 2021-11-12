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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Main implements ModInitializer {

	public static final ConfigurableRedstoneBlock CONFIGURABLE_REDSTONE_BLOCK = new ConfigurableRedstoneBlock(
		FabricBlockSettings.of(Material.METAL, MapColor.BRIGHT_RED)
		.requiresTool()
		.strength(5.0F, 6.0F)
		.sounds(BlockSoundGroup.METAL)
	);

	public static final ConfigurableTntBlock CONFIGURABLE_TNT_BLOCK = new ConfigurableTntBlock(
		FabricBlockSettings.of(Material.TNT)
		.breakInstantly()
		.sounds(BlockSoundGroup.GRASS)
	);

	public static EntityType<ConfigurableTntEntity> CONFIGURABLE_TNT_ENTITY;

	public static final CurvedRepeaterBlock CURVED_REPEATER_BLOCK = new CurvedRepeaterBlock(
		FabricBlockSettings.of(Material.DECORATION)
		.breakInstantly()
		.sounds(BlockSoundGroup.WOOD)
	);

	public static final MegaTntBlock MEGA_TNT_BLOCK = new MegaTntBlock(
		FabricBlockSettings.of(Material.TNT)
		.breakInstantly()
		.sounds(BlockSoundGroup.GRASS)
	);

	public static EntityType<MegaTntEntity> MEGA_TNT_ENTITY;

	public static final MemoryCellBlock MEMORY_CELL_BLOCK = new MemoryCellBlock(
		FabricBlockSettings.of(Material.DECORATION)
		.breakInstantly()
		.sounds(BlockSoundGroup.WOOD)
	);

	public static BlockEntityType<MemoryCellBlockEntity> MEMORY_CELL_BLOCK_ENTITY;

	public static final SmallMemoryCellBlock SMALL_MEMORY_CELL_BLOCK = new SmallMemoryCellBlock(
		FabricBlockSettings.of(Material.DECORATION)
		.breakInstantly()
		.sounds(BlockSoundGroup.WOOD)
	);

	public static BlockEntityType<SmallMemoryCellBlockEntity> SMALL_MEMORY_CELL_BLOCK_ENTITY;


	public static final String MOD_ID = "redstone_components";

	public static final String VERSION = "1.0.2"; // updated by updateVersion script with sed :)

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(new Commands());

		// memory cell
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, MemoryCellBlock.NAME), MEMORY_CELL_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, MemoryCellBlock.NAME),
			new BlockItem(MEMORY_CELL_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);
		MEMORY_CELL_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, MemoryCellBlock.NAME),
			FabricBlockEntityTypeBuilder.create(MemoryCellBlockEntity::new, MEMORY_CELL_BLOCK).build()
		);

		// small memory cell
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, SmallMemoryCellBlock.NAME), SMALL_MEMORY_CELL_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, SmallMemoryCellBlock.NAME),
			new BlockItem(SMALL_MEMORY_CELL_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);
		SMALL_MEMORY_CELL_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, SmallMemoryCellBlock.NAME),
			FabricBlockEntityTypeBuilder.create(SmallMemoryCellBlockEntity::new, SMALL_MEMORY_CELL_BLOCK).build()
		);

		// configurable redstone block
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, ConfigurableRedstoneBlock.NAME), CONFIGURABLE_REDSTONE_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, ConfigurableRedstoneBlock.NAME),
			new BlockItem(CONFIGURABLE_REDSTONE_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);

		// configurable tnt
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, ConfigurableTntBlock.NAME), CONFIGURABLE_TNT_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, ConfigurableTntBlock.NAME),
			new BlockItem(CONFIGURABLE_TNT_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);
		CONFIGURABLE_TNT_ENTITY = Registry.register(
			Registry.ENTITY_TYPE, new Identifier(MOD_ID, ConfigurableTntBlock.NAME),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, ConfigurableTntEntity::new)
			.fireImmune()
			.dimensions(EntityDimensions.fixed(0.98F, 0.98F))
			.trackRangeBlocks(10)
			.trackedUpdateRate(10)
			.build()
		);

		// mega tnt
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, MegaTntBlock.NAME), MEGA_TNT_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, MegaTntBlock.NAME),
			new BlockItem(MEGA_TNT_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);
		MEGA_TNT_ENTITY = Registry.register(
			Registry.ENTITY_TYPE, new Identifier(MOD_ID, MegaTntBlock.NAME),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, MegaTntEntity::create) // ::create is workaround bc MegaTntEntity has 1+ constructors. same problem here -> https://stackoverflow.com/q/45329062
			.fireImmune()
			.dimensions(EntityDimensions.fixed(1.98F, 1.98F))
			.trackRangeBlocks(10)
			.trackedUpdateRate(10)
			.build()
		);

		// curved repeater
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, CurvedRepeaterBlock.NAME), CURVED_REPEATER_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, CurvedRepeaterBlock.NAME),
			new BlockItem(CURVED_REPEATER_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);
	}
}
