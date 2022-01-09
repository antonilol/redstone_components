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

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class Commands implements CommandRegistrationCallback {

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		LiteralArgumentBuilder<ServerCommandSource> lock =

		literal("lock")
		.executes(c -> {
			ConfigurableRedstoneBlock.setLockedLast(true);
			return Command.SINGLE_SUCCESS;
		});

		LiteralArgumentBuilder<ServerCommandSource> unlock =

		literal("unlock")
		.executes(c -> {
			ConfigurableRedstoneBlock.setLockedLast(false);
			return Command.SINGLE_SUCCESS;
		});

		LiteralArgumentBuilder<ServerCommandSource> version =

		literal("version")
		.executes(c -> {
			c.getSource().sendFeedback(new LiteralText("redstone_components v" + Main.VERSION + " by Antoni Spaanderman"), false);
			c.getSource().sendFeedback(new LiteralText("GitHub: https://github.com/antonilol/redstone_components"), false); // TODO is it possible to add a clickable link here?
			return Command.SINGLE_SUCCESS;
		});

		dispatcher.register(
			literal("redstone_components")
			.then(lock)
			.then(unlock)
			.then(version)
		);

		dispatcher.register(lock);

		dispatcher.register(unlock);
	}
}
