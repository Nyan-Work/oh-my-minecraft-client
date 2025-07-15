package com.plusls.ommc.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The implementation for mc [1.14.4, ~)
 * <p>
 * Code from <a href="https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/api/client/command/v1/ClientCommandManager.java">Fabric Command API v1</a>
 * <p>
 * Manages client-sided commands and provides some related helper methods.
 *
 * <p>Client-sided commands are fully executed on the client,
 * so players can use them in both singleplayer and multiplayer.
 *
 * <p>Registrations can be done in the {@link #DISPATCHER} during a {@link net.fabricmc.api.ClientModInitializer}'s
 * initialization. (See example below.)
 *
 * <p>The commands are run on the client game thread by default.
 * Avoid doing any heavy calculations here as that can freeze the game's rendering.
 * For example, you can move heavy code to another thread.
 *
 * <p>This class also has alternatives to the server-side helper methods in
 * {@link net.minecraft.commands.Commands}:
 * {@link #literal(String)} and {@link #argument(String, ArgumentType)}.
 *
 * <p>The precedence rules of client-sided and server-sided commands with the same name
 * are an implementation detail that is not guaranteed to remain the same in future versions.
 * The aim is to make commands from the server take precedence over client-sided commands
 * in a future version of this API.
 *
 * <h2>Example command</h2>
 * <pre>
 * {@code
 * ClientCommandManager.DISPATCHER.register(
 * 	ClientCommandManager.literal("hello").executes(context -> {
 * 		context.getSource().sendFeedback(new LiteralText("Hello, world!"));
 * 		return 0;
 *    })
 * );
 * }
 * </pre>
 */
@Environment(EnvType.CLIENT)
public final class ClientCommandManager {
    /**
     * The command dispatcher that handles client command registration and execution.
     */
    public static final CommandDispatcher<FabricClientCommandSource> DISPATCHER = new CommandDispatcher<>();

    private ClientCommandManager() {
    }

    /**
     * Creates a literal argument builder.
     *
     * @param name the literal name
     * @return the created argument builder
     */
    public static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates a required argument builder.
     *
     * @param name the name of the argument
     * @param type the type of the argument
     * @param <T>  the type of the parsed argument value
     * @return the created argument builder
     */
    public static <T> RequiredArgumentBuilder<FabricClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}