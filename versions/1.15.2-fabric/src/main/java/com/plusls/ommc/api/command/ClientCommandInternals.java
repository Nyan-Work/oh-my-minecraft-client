package com.plusls.ommc.api.command;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.plusls.ommc.mixin.accessor.AccessorHelpCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.hendrixshen.magiclib.api.compat.minecraft.network.chat.ComponentCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The implementation for mc [1.14.4, ~)
 * <p>
 * Code from <a href="https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/impl/command/client/ClientCommandInternals.java">Fabric Command API v1</a>
 */
@Environment(EnvType.CLIENT)
public final class ClientCommandInternals {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final char PREFIX = '/';
    private static final String API_COMMAND_NAME = "fabric-command-api-v1:client";
    private static final String SHORT_API_COMMAND_NAME = "fcc";

    /**
     * Executes a client-sided command from a message.
     *
     * @param message the command message
     * @return true if the message should not be sent to the server, false otherwise
     */
    public static boolean executeCommand(String message) {
        if (message.isEmpty()) {
            return false; // Nothing to process
        }

        if (message.charAt(0) != PREFIX) {
            return false; // Incorrect prefix, won't execute anything.
        }

        Minecraft client = Minecraft.getInstance();

        // The interface is implemented on ClientCommandSource with a mixin.
        // noinspection ConstantConditions
        FabricClientCommandSource commandSource = (FabricClientCommandSource) client.getConnection().getSuggestionsProvider();

        client.getProfiler().push(message);

        try {
            // TODO: Check for server commands before executing.
            //   This requires parsing the command, checking if they match a server command
            //   and then executing the command with the parse results.
            ClientCommandManager.DISPATCHER.execute(message.substring(1), commandSource);
            return true;
        } catch (CommandSyntaxException e) {
            boolean ignored = isIgnoredException(e.getType());
            LOGGER.log(ignored ? Level.DEBUG : Level.WARN, "Syntax exception for client-sided command '{}'", message, e);

            if (ignored) {
                return false;
            }

            commandSource.sendError(getErrorMessage(e));
            return true;
        } catch (RuntimeException e) {
            LOGGER.warn("Error while executing client-sided command '{}'", message, e);
            commandSource.sendError(ComponentCompat.literal(e.getMessage()));
            return true;
        } finally {
            client.getProfiler().pop();
        }
    }

    /**
     * Tests whether a command syntax exception with the type
     * should be ignored and the message sent to the server.
     *
     * @param type the exception type
     * @return true if ignored, false otherwise
     */
    private static boolean isIgnoredException(CommandExceptionType type) {
        BuiltInExceptionProvider builtins = CommandSyntaxException.BUILT_IN_EXCEPTIONS;

        // Only ignore unknown commands and node parse exceptions.
        // The argument-related dispatcher exceptions are not ignored because
        // they will only happen if the user enters a correct command.
        return type == builtins.dispatcherUnknownCommand() || type == builtins.dispatcherParseException();
    }

    // See CommandSuggestor.method_30505. That cannot be used directly as it returns an OrderedText instead of a Text.
    private static Component getErrorMessage(CommandSyntaxException e) {
        Component message = ComponentUtils.fromMessage(e.getRawMessage());
        String context = e.getContext();

        return context != null ? ComponentCompat.translatable("command.context.parse_error", message, context) : message;
    }

    /**
     * Runs final initialization tasks such as {@link CommandDispatcher#findAmbiguities(AmbiguityConsumer)}
     * on the command dispatcher. Also registers a {@code /fcc help} command if there are other commands present.
     */
    public static void finalizeInit() {
        if (!ClientCommandManager.DISPATCHER.getRoot().getChildren().isEmpty()) {
            // Register an API command if there are other commands;
            // these helpers are not needed if there are no client commands
            LiteralArgumentBuilder<FabricClientCommandSource> help = ClientCommandManager.literal("help");
            help.executes(ClientCommandInternals::executeRootHelp);
            help.then(ClientCommandManager.argument("command", StringArgumentType.greedyString()).executes(ClientCommandInternals::executeArgumentHelp));

            CommandNode<FabricClientCommandSource> mainNode = ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal(API_COMMAND_NAME).then(help));
            ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal(SHORT_API_COMMAND_NAME).redirect(mainNode));
        }

        // noinspection CodeBlock2Expr
        ClientCommandManager.DISPATCHER.findAmbiguities((parent, child, sibling, inputs) -> {
            LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", ClientCommandManager.DISPATCHER.getPath(child), ClientCommandManager.DISPATCHER.getPath(sibling), inputs);
        });
    }

    private static int executeRootHelp(CommandContext<FabricClientCommandSource> context) {
        return executeHelp(ClientCommandManager.DISPATCHER.getRoot(), context);
    }

    private static int executeArgumentHelp(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        ParseResults<FabricClientCommandSource> parseResults = ClientCommandManager.DISPATCHER.parse(StringArgumentType.getString(context, "command"), context.getSource());
        List<ParsedCommandNode<FabricClientCommandSource>> nodes = parseResults.getContext().getNodes();

        if (nodes.isEmpty()) {
            throw AccessorHelpCommand.getFailedException().create();
        }

        return executeHelp(Iterables.getLast(nodes).getNode(), context);
    }

    private static int executeHelp(CommandNode<FabricClientCommandSource> startNode, CommandContext<FabricClientCommandSource> context) {
        Map<CommandNode<FabricClientCommandSource>, String> commands = ClientCommandManager.DISPATCHER.getSmartUsage(startNode, context.getSource());

        for (String command : commands.values()) {
            context.getSource().sendFeedback(ComponentCompat.literal("/" + command));
        }

        return commands.size();
    }

    public static void addCommands(CommandDispatcher<FabricClientCommandSource> target, FabricClientCommandSource source) {
        Map<CommandNode<FabricClientCommandSource>, CommandNode<FabricClientCommandSource>> originalToCopy = new HashMap<>();
        originalToCopy.put(ClientCommandManager.DISPATCHER.getRoot(), target.getRoot());
        copyChildren(ClientCommandManager.DISPATCHER.getRoot(), target.getRoot(), source, originalToCopy);
    }

    /**
     * Copies the child commands from origin to target, filtered by {@code child.canUse(source)}.
     * Mimics vanilla's CommandManager.makeTreeForSource.
     *
     * @param origin         the source command node
     * @param target         the target command node
     * @param source         the command source
     * @param originalToCopy a mutable map from original command nodes to their copies, used for redirects;
     *                       should contain a mapping from origin to target
     */
    private static void copyChildren(
            CommandNode<FabricClientCommandSource> origin,
            CommandNode<FabricClientCommandSource> target,
            FabricClientCommandSource source,
            Map<CommandNode<FabricClientCommandSource>, CommandNode<FabricClientCommandSource>> originalToCopy
    ) {
        for (CommandNode<FabricClientCommandSource> child : origin.getChildren()) {
            if (!child.canUse(source)) continue;

            ArgumentBuilder<FabricClientCommandSource, ?> builder = child.createBuilder();

            // Reset the unnecessary non-completion stuff from the builder
            builder.requires(s -> true); // This is checked with the if check above.

            if (builder.getCommand() != null) {
                builder.executes(context -> 0);
            }

            // Set up redirects
            if (builder.getRedirect() != null) {
                builder.redirect(originalToCopy.get(builder.getRedirect()));
            }

            CommandNode<FabricClientCommandSource> result = builder.build();
            originalToCopy.put(child, result);
            target.addChild(result);

            if (!child.getChildren().isEmpty()) {
                copyChildren(child, result, source, originalToCopy);
            }
        }
    }
}