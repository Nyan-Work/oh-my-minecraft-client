package com.plusls.ommc.api.command;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.Util;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.api.compat.minecraft.network.chat.ComponentCompat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

// Modified from brigadier
public class ClientEntityAnchorArgument implements ArgumentType<ClientEntityAnchorArgument.Anchor> {
    private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
    private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(
            object -> ComponentCompat.translatable("argument.anchor.invalid", object)
    );

    public static ClientEntityAnchorArgument.Anchor getAnchor(@NotNull CommandContext<FabricClientCommandSource> commandContext, String string) {
        return commandContext.getArgument(string, ClientEntityAnchorArgument.Anchor.class);
    }

    public static ClientEntityAnchorArgument anchor() {
        return new ClientEntityAnchorArgument();
    }

    @Override
    public ClientEntityAnchorArgument.Anchor parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        String string = stringReader.readUnquotedString();
        ClientEntityAnchorArgument.Anchor anchor = ClientEntityAnchorArgument.Anchor.getByName(string);
        if (anchor == null) {
            stringReader.setCursor(i);
            throw ERROR_INVALID.createWithContext(stringReader, string);
        } else {
            return anchor;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggest(ClientEntityAnchorArgument.Anchor.BY_NAME.keySet(), suggestionsBuilder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static enum Anchor {
        FEET("feet", (vec3, entity) -> vec3),
        EYES("eyes", (vec3, entity) -> new Vec3(vec3.x, vec3.y + (double)entity.getEyeHeight(), vec3.z));

        static final Map<String, ClientEntityAnchorArgument.Anchor> BY_NAME = Util.make(Maps.newHashMap(), hashMap -> {
            for(ClientEntityAnchorArgument.Anchor anchor : values()) {
                hashMap.put(anchor.name, anchor);
            }
        });

        private final String name;
        private final BiFunction<Vec3, Entity, Vec3> transform;

        Anchor(String string2, BiFunction<Vec3, Entity, Vec3> biFunction) {
            this.name = string2;
            this.transform = biFunction;
        }

        @Nullable
        public static ClientEntityAnchorArgument.Anchor getByName(String string) {
            return BY_NAME.get(string);
        }

        public Vec3 apply(Entity entity) {
            return this.transform.apply(entity.position(), entity);
        }

        public Vec3 apply(@NotNull FabricClientCommandSource commandSourceStack) {
            Entity entity = commandSourceStack.getEntity();
            return entity == null ? commandSourceStack.getPosition() : this.transform.apply(commandSourceStack.getPosition(), entity);
        }
    }
}
