package com.plusls.ommc.api.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.hendrixshen.magiclib.api.compat.minecraft.network.chat.ComponentCompat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

// Modified from brigadier
public class ClientBlockPosArgument implements ArgumentType<ClientCoordinates> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
    public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType(ComponentCompat.translatable("argument.pos.unloaded"));
    public static final SimpleCommandExceptionType ERROR_OUT_OF_WORLD = new SimpleCommandExceptionType(ComponentCompat.translatable("argument.pos.outofworld"));
    public static final SimpleCommandExceptionType ERROR_OUT_OF_BOUNDS = new SimpleCommandExceptionType(ComponentCompat.translatable("argument.pos.outofbounds"));

    @Contract(value = " -> new", pure = true)
    public static @NotNull ClientBlockPosArgument blockPos() {
        return new ClientBlockPosArgument();
    }

    public static BlockPos getLoadedBlockPos(@NotNull CommandContext<FabricClientCommandSource> context, String name) throws CommandSyntaxException {
        ClientLevel clientLevel = context.getSource().getWorld();
        return getLoadedBlockPos(context, clientLevel, name);
    }

    public static BlockPos getLoadedBlockPos(CommandContext<FabricClientCommandSource> context, @NotNull ClientLevel clientLevel, String name) throws CommandSyntaxException {
        BlockPos blockPos = getBlockPos(context, name);

        if (!clientLevel.isLoaded(blockPos)) {
            throw ERROR_NOT_LOADED.create();
        } else if (!clientLevel.isInWorldBounds(blockPos)) {
            throw ERROR_OUT_OF_WORLD.create();
        } else {
            return blockPos;
        }
    }

    public static BlockPos getBlockPos(@NotNull CommandContext<FabricClientCommandSource> commandContext, String string) {
        return commandContext.getArgument(string, ClientCoordinates.class).getBlockPos(commandContext.getSource());
    }

    //#if MC > 11502
    public static BlockPos getSpawnablePos(CommandContext<FabricClientCommandSource> commandContext, String string) throws CommandSyntaxException {
        BlockPos blockPos = getBlockPos(commandContext, string);
        if (!Level.isInSpawnableBounds(blockPos)) {
            throw ERROR_OUT_OF_BOUNDS.create();
        } else {
            return blockPos;
        }
    }
    //#endif

    public ClientCoordinates parse(@NotNull StringReader stringReader) throws CommandSyntaxException {
        return stringReader.canRead() && stringReader.peek() == '^' ? ClientLocalCoordinates.parse(stringReader) : ClientWorldCoordinates.parseInt(stringReader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        if (!(commandContext.getSource() instanceof SharedSuggestionProvider)) {
            return Suggestions.empty();
        } else {
            String string = suggestionsBuilder.getRemaining();
            Collection<SharedSuggestionProvider.TextCoordinates> collection;
            if (!string.isEmpty() && string.charAt(0) == '^') {
                collection = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
            } else {
                collection = ((SharedSuggestionProvider)commandContext.getSource()).getRelevantCoordinates();
            }

            return SharedSuggestionProvider.suggestCoordinates(string, collection, suggestionsBuilder, Commands.createValidator(this::parse));
        }
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
