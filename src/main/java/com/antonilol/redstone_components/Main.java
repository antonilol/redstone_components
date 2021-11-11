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
import net.minecraft.block.Block;
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

	public static final BentRepeaterBlock BENT_REPEATER_BLOCK = new BentRepeaterBlock(
		FabricBlockSettings.of(Material.DECORATION)
		.breakInstantly()
		.sounds(BlockSoundGroup.WOOD)
	);

	public static final String BENT_REPEATER_NAME = "bent_repeater";

	public static final Block CONFIGURABLE_REDSTONE_BLOCK = new ConfigurableRedstoneBlock(
		FabricBlockSettings.of(Material.METAL, MapColor.BRIGHT_RED)
		.requiresTool()
		.strength(5.0F, 6.0F)
		.sounds(BlockSoundGroup.METAL)
	);

	public static final String CONFIGURABLE_REDSTONE_BLOCK_NAME = "configurable_redstone_block";

	public static final Block CONFIGURABLE_TNT_BLOCK = new ConfigurableTntBlock(
		FabricBlockSettings.of(Material.TNT)
		.breakInstantly()
		.sounds(BlockSoundGroup.GRASS)
	);

	public static final String CONFIGURABLE_TNT_BLOCK_NAME = "configurable_tnt";

	public static EntityType<ConfigurableTntEntity> CONFIGURABLE_TNT_ENTITY;

	public static final Block MEGA_TNT_BLOCK = new MegaTntBlock(
		FabricBlockSettings.of(Material.TNT)
		.breakInstantly()
		.sounds(BlockSoundGroup.GRASS)
	);

	public static EntityType<MegaTntEntity> MEGA_TNT_ENTITY;

	public static final String MEGA_TNT_NAME = "mega_tnt";

	public static final Block MEMORY_CELL_BLOCK = new MemoryCellBlock(
		FabricBlockSettings.of(Material.DECORATION)
		.breakInstantly()
		.sounds(BlockSoundGroup.WOOD)
	);

	public static BlockEntityType<MemoryCellBlockEntity> MEMORY_CELL_BLOCK_ENTITY;

	public static final String MEMORY_CELL_NAME = "memory_cell";

	public static final String MOD_ID = "redstone_components";

	public static final Block SMALL_MEMORY_CELL_BLOCK = new SmallMemoryCellBlock(
		FabricBlockSettings.of(Material.DECORATION)
		.breakInstantly()
		.sounds(BlockSoundGroup.WOOD)
	);

	public static BlockEntityType<SmallMemoryCellBlockEntity> SMALL_MEMORY_CELL_BLOCK_ENTITY;

	public static final String SMALL_MEMORY_CELL_NAME = "small_memory_cell";


	public static final String VERSION = "1.0.2"; // updated by updateVersion script with sed :)

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(new Commands());

		// memory cell
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, MEMORY_CELL_NAME), MEMORY_CELL_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, MEMORY_CELL_NAME),
			new BlockItem(MEMORY_CELL_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);
		MEMORY_CELL_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, MEMORY_CELL_NAME),
			FabricBlockEntityTypeBuilder.create(MemoryCellBlockEntity::new, MEMORY_CELL_BLOCK).build()
		);

		// small memory cell
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, SMALL_MEMORY_CELL_NAME), SMALL_MEMORY_CELL_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, SMALL_MEMORY_CELL_NAME),
			new BlockItem(SMALL_MEMORY_CELL_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);
		SMALL_MEMORY_CELL_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, SMALL_MEMORY_CELL_NAME),
			FabricBlockEntityTypeBuilder.create(SmallMemoryCellBlockEntity::new, SMALL_MEMORY_CELL_BLOCK).build()
		);

		// configurable redstone block
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, CONFIGURABLE_REDSTONE_BLOCK_NAME), CONFIGURABLE_REDSTONE_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, CONFIGURABLE_REDSTONE_BLOCK_NAME),
			new BlockItem(CONFIGURABLE_REDSTONE_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);

		// configurable tnt
//		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, CONFIGURABLE_TNT_BLOCK_NAME), CONFIGURABLE_TNT_BLOCK);
//		Registry.register(
//			Registry.ITEM, new Identifier(MOD_ID, CONFIGURABLE_TNT_BLOCK_NAME),
//			new BlockItem(CONFIGURABLE_TNT_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
//		);
//		CONFIGURABLE_TNT_ENTITY = Registry.register(
//			Registry.ENTITY_TYPE, new Identifier(MOD_ID, CONFIGURABLE_TNT_BLOCK_NAME),
//			FabricEntityTypeBuilder.create(SpawnGroup.MISC, ConfigurableTntEntity::new)
//			.fireImmune()
//			.dimensions(EntityDimensions.fixed(0.98F, 0.98F))
//			.trackRangeBlocks(10)
//			.trackedUpdateRate(10)
//			.build()
//		);

		// mega tnt
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, MEGA_TNT_NAME), MEGA_TNT_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, MEGA_TNT_NAME),
			new BlockItem(MEGA_TNT_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);
		MEGA_TNT_ENTITY = Registry.register(
			Registry.ENTITY_TYPE, new Identifier(MOD_ID, MEGA_TNT_NAME),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, MegaTntEntity::create) // ::create is workaround bc MegaTntEntity has 1+ constructors. same problem here -> https://stackoverflow.com/q/45329062
			.fireImmune()
			.dimensions(EntityDimensions.fixed(1.98F, 1.98F))
			.trackRangeBlocks(10)
			.trackedUpdateRate(10)
			.build()
		);

		// bent repeater
		Registry.register(Registry.BLOCK, new Identifier(MOD_ID, BENT_REPEATER_NAME), BENT_REPEATER_BLOCK);
		Registry.register(
			Registry.ITEM, new Identifier(MOD_ID, BENT_REPEATER_NAME),
			new BlockItem(BENT_REPEATER_BLOCK, new FabricItemSettings().group(ItemGroup.REDSTONE))
		);
	}
}
