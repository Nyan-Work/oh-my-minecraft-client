package com.plusls.ommc.impl.generic.highlightWaypoint;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.CommandContext;
import com.plusls.ommc.SharedConstants;
import com.plusls.ommc.api.command.ClientBlockPosArgument;
import com.plusls.ommc.game.Configs;
import com.plusls.ommc.mixin.accessor.AccessorTextComponent;
import com.plusls.ommc.mixin.accessor.AccessorTranslatableComponent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.api.compat.minecraft.network.chat.ComponentCompat;
import top.hendrixshen.magiclib.api.compat.minecraft.network.chat.StyleCompat;
import top.hendrixshen.magiclib.impl.compat.minecraft.world.level.dimension.DimensionWrapper;
import top.hendrixshen.magiclib.util.minecraft.ComponentUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//#if MC > 11901
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//#endif

//#if MC > 11802
import net.minecraft.network.chat.contents.*;
//#endif

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HighlightWaypointHandler {
    @Getter
    private static final HighlightWaypointHandler instance = new HighlightWaypointHandler();
    private static final String highlightWaypoint = "highlightWaypoint";
    private static final Pattern pattern = Pattern.compile("(?:(?:x\\s*:\\s*)?(?<x>(?:[+-]?\\d+)(?:\\.\\d+)?)(?:[df])?)(?:(?:(?:\\s*[,，]\\s*(?:y\\s*:\\s*)?)|(?:\\s+))(?<y>(?:[+-]?\\d+)(?:\\.\\d+)?)(?:[df])?)?(?:(?:(?:\\s*[,，]\\s*(?:z\\s*:\\s*)?)|(?:\\s+))(?<z>(?:[+-]?\\d+)(?:\\.\\d+)?)(?:[df])?)", Pattern.CASE_INSENSITIVE);

    private final MutablePair<BlockPos, BlockPos> highlightPos = MutablePair.of(null, null);
    private final HighlightWaypointRenderer renderer = HighlightWaypointRenderer.getInstance();

    public static void init() {
        //#if MC > 11901
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
        //#else
        //$$ ClientCommandManager.DISPATCHER.register(
        //#endif
                ClientCommandManager.literal(HighlightWaypointHandler.highlightWaypoint).then(
                        ClientCommandManager.argument("pos", ClientBlockPosArgument.blockPos())
                                .executes(HighlightWaypointHandler.instance::runCommand)
        //#if MC > 11901
                )));
        //#else
        //$$ ));
        //#endif
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> HighlightWaypointHandler.instance.clearHighlightPos());
        HighlightWaypointRenderer.init();
        HighlightWaypointResourceLoader.init();
    }

    private int runCommand(CommandContext<FabricClientCommandSource> context) {
        BlockPos pos = ClientBlockPosArgument.getBlockPos(context, "pos");
        this.setHighlightPos(pos, false);

        return 0;
    }

    private @NotNull List<HighlightWaypointHandler.ParseResult> parsePositions(@NotNull String message) {
        List<HighlightWaypointHandler.ParseResult> ret = Lists.newArrayList();
        Matcher matcher = HighlightWaypointHandler.pattern.matcher(message);

        while (matcher.find()) {
            ret.add(this.parsePosition(matcher));
        }

        ret.removeIf(Objects::isNull);
        ret.sort(Comparator.comparingInt(HighlightWaypointHandler.ParseResult::getMatcherStart));
        return ret;
    }

    private @Nullable HighlightWaypointHandler.ParseResult parsePosition(@NotNull Matcher matcher) {
        Integer x = null;
        int y = 64;
        Integer z = null;
        String xStr = matcher.group("x");
        String yStr = matcher.group("y");
        String zStr = matcher.group("z");

        try {
            x = xStr.contains(".") ? (int) Double.parseDouble(xStr) : Integer.parseInt(xStr);
            z = zStr.contains(".") ? (int) Double.parseDouble(zStr) : Integer.parseInt(zStr);

            if (yStr != null) {
                y = zStr.contains(".") ? (int) Double.parseDouble(yStr) : Integer.parseInt(yStr);
            }
        } catch (NumberFormatException e) {
            SharedConstants.getLogger().error("Failed to parse coordinate {}: {}", matcher.group(), e);
        }

        if (x == null || z == null) {
            return null;
        }

        return new HighlightWaypointHandler.ParseResult(matcher.group(), new BlockPos(x, y, z), matcher.start());
    }

    public void parseMessage(@NotNull Component chat) {
        chat.getSiblings().forEach(this::parseMessage);

        //#if MC > 11802
        ComponentContents
        //#else MC > 11502
        //$$ BaseComponent
        //#endif
        contents = ComponentUtil.getTextContent((MutableComponent) chat);

        if (
                //#if MC > 11802
                !(contents instanceof TranslatableContents)
                //#else
                //$$ !(contents instanceof TranslatableComponent)
                //#endif
        ) {
            this.updateMessage(chat);
            return;
        }

        //#if MC > 11802
        Object[] args = ((TranslatableContents) contents).getArgs();
        //#else
        //$$ Object[] args = ((TranslatableComponent) contents).getArgs();
        //#endif
        boolean updateTranslatableText = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Component) {
                this.parseMessage(ComponentUtil.simple(((Component) args[i]).getString()));
            } else if (args[i] instanceof String) {
                Component text = ComponentUtil.simple(args[i]);

                if (this.updateMessage(text)) {
                    args[i] = text;
                    updateTranslatableText = true;
                }
            }
        }

        if (updateTranslatableText) {
            //#if MC > 11502
            ((AccessorTranslatableComponent) contents).setDecomposedWith(null);
            //#else
            //$$ ((AccessorTranslatableComponent) contents).setDecomposedLanguageTime(-1);
            //#endif
        }

        this.updateMessage(chat);
    }

    private boolean updateMessage(@NotNull Component chat) {
        //#if MC > 11802
        ComponentContents
        //#else MC > 11502
        //$$ BaseComponent
        //#endif
        contents = ComponentUtil.getTextContent((MutableComponent) chat);

        if (
            //#if MC > 12002
                !(contents instanceof PlainTextContents.LiteralContents)
            //#elseif MC > 11802
            //$$ !(contents instanceof LiteralContents)
            //#else
            //$$ !(contents instanceof TextComponent)
            //#endif
        ) {
            return false;
        }

        //#if MC > 12002
        PlainTextContents.LiteralContents literalChatText = (PlainTextContents.LiteralContents) contents;
        //#elseif MC > 11802
        //$$ LiteralContents literalChatText = (LiteralContents) contents;
        //#else
        //$$ TextComponent literalChatText = (TextComponent) contents;
        //#endif
        String message = ((AccessorTextComponent) (Object) literalChatText).getText();
        List<HighlightWaypointHandler.ParseResult> positions = this.parsePositions(message);

        if (positions.isEmpty()) {
            return false;
        }

        StyleCompat originalStyle = StyleCompat.of(chat.getStyle());
        ClickEvent originalClickEvent = originalStyle.get().getClickEvent();
        ArrayList<ComponentCompat> texts = Lists.newArrayList();
        int prevIdx = 0;

        // Rebuild components.
        for (HighlightWaypointHandler.ParseResult position : positions) {
            String waypointString = position.getText();
            int waypointIdx = position.getMatcherStart();
            texts.add(ComponentCompat.literalCompat(message.substring(prevIdx, waypointIdx)).withStyle(originalStyle));
            BlockPos pos = position.getPos();
            texts.add(ComponentCompat.literalCompat(waypointString)
                    .withStyle(ChatFormatting.GREEN)
                    .withStyle(ChatFormatting.UNDERLINE)
                    .withStyle(style -> style.withClickEvent(originalClickEvent == null ||
                            Configs.forceParseWaypointFromChat.getBooleanValue() ? new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            String.format("/%s %d %d %d", HighlightWaypointHandler.highlightWaypoint, pos.getX(), pos.getY(), pos.getZ())) :
                            originalClickEvent))
                    .withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            ComponentCompat.literal(SharedConstants.getTranslation("highlight_waypoint.tooltip"))
                    ))));
            prevIdx = waypointIdx + waypointString.length();
        }

        // Add tail if existed.
        if (prevIdx < message.length()) {
            texts.add(ComponentCompat.literalCompat(message.substring(prevIdx)).withStyle(originalStyle));
        }

        texts.forEach(value -> chat.getSiblings().add(value.get()));
        ((AccessorTextComponent) (Object) literalChatText).setText("");
        //#if MC > 11502
        ((MutableComponent) chat).withStyle(StyleCompat.empty());
        //#else
        //$$ ((BaseComponent) chat).withStyle();
        //#endif
        return true;
    }

    public void setHighlightPos(@NotNull BlockPos pos, boolean directHighlight) {
        Player player = Minecraft.getInstance().player;

        if (player == null) {
            return;
        }

        boolean posChanged;

        if (this.inOverworld(player)) {
            posChanged = this.setHighlightBlockPos(
                    pos, new BlockPos(pos.getX() / 8, pos.getY(), pos.getZ() / 8));
        } else if (this.inNether(player)) {
            posChanged = this.setHighlightBlockPos(
                    new BlockPos(pos.getX() * 8, pos.getY(), pos.getZ() * 8), pos);
        } else {
            posChanged = this.setHighlightBlockPos(pos, pos);
        }

        if (directHighlight || !posChanged) {
            this.renderer.lastBeamTime = System.currentTimeMillis() + Configs.highlightBeamTime.getIntegerValue() * 1000L;
        }
    }

    public BlockPos getHighlightPos() {
        Player player = Minecraft.getInstance().player;
        return player == null ? BlockPos.ZERO : this.getHighlightPos(player);
    }

    public BlockPos getHighlightPos(Player player) {
        return this.inNether(player) ? this.highlightPos.getRight() : this.highlightPos.getLeft();
    }

    private boolean setHighlightBlockPos(@NotNull BlockPos overworldPos, @NotNull BlockPos netherWorldPos) {
        if (overworldPos.equals(this.highlightPos.getLeft()) &&
                netherWorldPos.equals(this.highlightPos.getRight())) {
            return false;
        }

        this.highlightPos.setLeft(overworldPos);
        this.highlightPos.setRight(netherWorldPos);
        return true;
    }

    public void clearHighlightPos() {
        this.highlightPos.setLeft(null);
        this.highlightPos.setRight(null);
        this.renderer.lastBeamTime = 0;
    }

    private boolean inOverworld(@NotNull Player player) {
        return DimensionWrapper.of(player).equals(DimensionWrapper.OVERWORLD);
    }

    private boolean inNether(@NotNull Player player) {
        return DimensionWrapper.of(player).equals(DimensionWrapper.NETHER);
    }

    @Getter
    @AllArgsConstructor
    public static class ParseResult {
        private final String text;
        private final BlockPos pos;
        private final int matcherStart;
    }
}
