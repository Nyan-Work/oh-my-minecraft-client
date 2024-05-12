package com.plusls.ommc.feature.sortInventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.Window;
import com.plusls.ommc.api.sortInventory.IDyeBlock;
import com.plusls.ommc.config.Configs;
import com.plusls.ommc.mixin.accessor.AccessorAbstractContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.hendrixshen.magiclib.compat.minecraft.api.world.item.ItemStackCompatApi;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//#if MC > 11902
import net.minecraft.core.registries.BuiltInRegistries;
//#else
//$$ import net.minecraft.core.Registry;
//#endif

//#if MC < 11600
//$$ import com.plusls.ommc.mixin.accessor.AccessorBlock;
//#endif

public class SortInventoryUtil {
    public static final int SLOT_CLICKED_OUTSIDE = -999;
    private static boolean allShulkerBox;
    private static final Map<DyeColor, Integer> DYE_COLOR_MAPPING = Maps.newHashMap();
    private static final Map<MapColor, Integer> MAP_COLOR_MAPPING = Maps.newHashMap();

    static {
        SortInventoryUtil.DYE_COLOR_MAPPING.put(null, 0);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.WHITE, 1);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.LIGHT_GRAY, 2);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.GRAY, 3);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.BLACK, 4);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.BROWN, 5);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.RED, 6);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.ORANGE, 7);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.YELLOW, 8);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.LIME, 9);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.GREEN, 10);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.CYAN, 11);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.LIGHT_BLUE, 12);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.BLUE, 13);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.PURPLE, 14);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.MAGENTA, 15);
        SortInventoryUtil.DYE_COLOR_MAPPING.put(DyeColor.PINK, 16);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(null, 0);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.SNOW, 1);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_LIGHT_GRAY, 2);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_GRAY, 3);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_BLACK, 4);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_BROWN, 5);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_RED, 6);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_ORANGE, 7);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_YELLOW, 8);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_LIGHT_GREEN, 9);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_GREEN, 10);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_CYAN, 11);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_LIGHT_BLUE, 12);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_BLUE, 13);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_PURPLE, 14);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_MAGENTA, 15);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.COLOR_PINK, 16);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_WHITE, 1);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_LIGHT_GRAY, 2);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_GRAY, 3);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_BLACK, 4);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_BROWN, 5);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_RED, 6);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_ORANGE, 7);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_YELLOW, 8);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_LIGHT_GREEN, 9);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_GREEN, 10);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_CYAN, 11);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_LIGHT_BLUE, 12);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_BLUE, 13);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_PURPLE, 14);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_MAGENTA, 15);
        SortInventoryUtil.MAP_COLOR_MAPPING.put(MapColor.TERRACOTTA_PINK, 16);
    }

    @Nullable
    public static Tuple<Integer, Integer> getSortRange(AbstractContainerMenu screenHandler, @NotNull Slot mouseSlot) {
        int mouseIdx = mouseSlot.index;

        if (mouseIdx == 0 && mouseSlot.getContainerSlot() != 0) {
            mouseIdx = mouseSlot.getContainerSlot();
        }

        int l = mouseIdx;
        int r = mouseIdx + 1;

        Class<?> clazz = screenHandler.slots.get(mouseIdx).container.getClass();

        for (int i = mouseIdx - 1; i >= 0; i--) {
            if (clazz != screenHandler.slots.get(i).container.getClass()) {
                l = i + 1;
                break;
            } else if (i == 0) {
                l = 0;
            }
        }

        for (int i = mouseIdx + 1; i < screenHandler.slots.size(); i++) {
            if (clazz != screenHandler.slots.get(i).container.getClass()) {
                r = i;
                break;
            } else if (i == screenHandler.slots.size() - 1) {
                r = screenHandler.slots.size();
            }
        }

        if (mouseSlot.container instanceof Inventory) {
            if (l == 5 && r == 46) {
                if (mouseIdx >= 9 && mouseIdx < 36) {
                    return new Tuple<>(9, 36);
                } else if (mouseIdx >= 36 && mouseIdx < 45) {
                    return new Tuple<>(36, 45);
                }
                return null;
            } else if (r - l == 36) {
                if (mouseIdx >= l && mouseIdx < l + 27) {
                    return new Tuple<>(l, l + 27);
                } else {
                    return new Tuple<>(l + 27, r);
                }
            }
        }

        if (l + 1 == r) {
            return null;
        }

        return new Tuple<>(l, r);
    }

    public static @Nullable Runnable sort() {
        Minecraft client = Minecraft.getInstance();

        if (!(client.screen instanceof AbstractContainerScreen<?>) ||
                client.screen instanceof CreativeModeInventoryScreen) {
            return null;
        }

        AbstractContainerScreen<?> handledScreen = (AbstractContainerScreen<?>) client.screen;
        Window window = client.getWindow();
        double x = client.mouseHandler.xpos() * window.getGuiScaledWidth() / window.getScreenWidth();
        double y = client.mouseHandler.ypos() * window.getGuiScaledHeight() / window.getScreenHeight();
        Slot mouseSlot = ((AccessorAbstractContainerScreen) handledScreen).invokeFindSlot(x, y);

        if (mouseSlot == null) {
            return null;
        }

        LocalPlayer player = client.player;

        if (client.gameMode == null || player == null) {
            return null;
        }

        AbstractContainerMenu screenHandler = player.containerMenu;
        Tuple<Integer, Integer> sortRange = SortInventoryUtil.getSortRange(screenHandler, mouseSlot);

        if (sortRange == null) {
            return null;
        }

        List<ItemStack> itemStacks = Lists.newArrayList();
        //#if MC > 11605
        ItemStack cursorStack = screenHandler.getCarried().copy();
        //#else
        //$$ ItemStack cursorStack = player.inventory.getCarried().copy();
        //#endif
        screenHandler.slots.stream().map(slot -> slot.getItem().copy()).forEach(itemStacks::add);
        // Merge picked item to inventory.
        List<Integer> mergeQueue = SortInventoryUtil.mergeItems(cursorStack, itemStacks, sortRange.getA(),
                sortRange.getB());
        List<Tuple<Integer, Integer>> swapQueue = SortInventoryUtil.quickSort(itemStacks, sortRange.getA(),
                sortRange.getB());
        SortInventoryUtil.doClick(player, screenHandler.containerId, client.gameMode, mergeQueue, swapQueue);
        return (!mergeQueue.isEmpty() || !swapQueue.isEmpty()) ?
                () -> Minecraft.getInstance().getSoundManager()
                        .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F)) :
                () -> Minecraft.getInstance().getSoundManager()
                        .play(SimpleSoundInstance.forUI(SoundEvents.DISPENSER_FAIL, 1.0F));
    }

    public static void doClick(Player player, int syncId, @NotNull MultiPlayerGameMode interactionManager,
                               @NotNull List<Integer> mergeQueue, List<Tuple<Integer, Integer>> swapQueue) {
        for (Integer slotId : mergeQueue) {
            if (slotId < 0 && slotId != SortInventoryUtil.SLOT_CLICKED_OUTSIDE) {
                // 放入打捆包需要右键
                interactionManager.handleInventoryMouseClick(syncId, -slotId, 1, ClickType.PICKUP, player);
            } else {
                interactionManager.handleInventoryMouseClick(syncId, slotId, 0, ClickType.PICKUP, player);
            }
        }

        for (Tuple<Integer, Integer> slotIdPair : swapQueue) {
            interactionManager.handleInventoryMouseClick(syncId, slotIdPair.getA(), 0, ClickType.PICKUP, player);
            interactionManager.handleInventoryMouseClick(syncId, slotIdPair.getB(), 0, ClickType.PICKUP, player);
            interactionManager.handleInventoryMouseClick(syncId, slotIdPair.getA(), 0, ClickType.PICKUP, player);
        }
    }

    private static boolean canStackAddMore(@NotNull ItemStack existingStack, ItemStack stack) {
        return !existingStack.isEmpty() &&
                ItemStackCompatApi.isSameItemSameTags(existingStack, stack) &&
                ShulkerBoxItemUtil.isStackable(existingStack) &&
                existingStack.getCount() < ShulkerBoxItemUtil.getMaxCount(existingStack) &&
                existingStack.getCount() < 64;
    }

    public static @NotNull List<Integer> addItemStack(List<ItemStack> itemStacks, ItemStack stackToAdd,
                                                      int beginSlot, int endSlot) {
        // merge in [beginSlot, endSlot)
        List<Integer> ret = Lists.newArrayList();

        for (int i = beginSlot; i < endSlot; i++) {
            ItemStack stack = itemStacks.get(i);

            if (stack.isEmpty()) {
                continue;
            }

            if (SortInventoryUtil.canStackAddMore(stack, stackToAdd)) {
                int addNum = ShulkerBoxItemUtil.getMaxCount(stack) - stack.getCount();

                if (addNum <= 0) {
                    continue;
                }

                ret.add(i);

                if (addNum >= stackToAdd.getCount()) {
                    stack.grow(stackToAdd.getCount());
                    stackToAdd.shrink(stackToAdd.getCount());
                    break;
                } else {
                    stack.grow(addNum);
                    stackToAdd.shrink(addNum);
                }
            }
            // TODO
//            else if (stack.getItem() instanceof BundleItem) {
//                CompoundTag nbtCompound = stack.getOrCreateTag();
//                ListTag nbtList = nbtCompound.getList("Items", Tag.TAG_COMPOUND);
//                Optional<CompoundTag> optional = BundleItem.getMatchingItem(stackToAdd, nbtList);
//                if (optional.isPresent()) {
//                    stackToAdd.shrink(BundleItem.add(stack, stackToAdd));
//                    ret.add(-i);
//                    if (stackToAdd.isEmpty()) {
//                        break;
//                    }
//                }
//            }
        }
        return ret;
    }

    private static int getItemId(@NotNull ItemStack itemStack) {
        return Item.getId(itemStack.getItem());
    }

    private static @NotNull List<Tuple<Integer, Integer>> quickSort(List<ItemStack> itemStacks,
                                                                    int startSlot, int endSlot) {
        // sort [startSlot, endSlot)
        List<Tuple<Integer, Integer>> ret = Lists.newArrayList();
        List<ItemStack> sortedItemStacks = Lists.newArrayList();
        allShulkerBox = true;

        for (int i = startSlot; i < endSlot; ++i) {
            ItemStack itemStack = itemStacks.get(i);

            if (!itemStack.isEmpty() && !ShulkerBoxItemUtil.isShulkerBoxBlockItem(itemStack)) {
                allShulkerBox = false;
            }

            sortedItemStacks.add(itemStack);
        }

        try {
            sortedItemStacks.sort(new ItemStackComparator());
        } catch (IllegalArgumentException e) {
            OhMyMinecraftClientReference.getLogger().info("An IllegalArgumentException caught. Run comparator test.");
            ItemStackComparator comparator = new ItemStackComparator();
            for (int i = 0; i < endSlot; i++) {
                for (int j = i + 1; j < endSlot; j++) {
                    ItemStack stack1 = sortedItemStacks.get(i);
                    ItemStack stack2 = sortedItemStacks.get(j);

                    int res1 = comparator.compare(stack1, stack2);
                    int res2 = comparator.compare(stack2, stack1);

                    if ( !( ( res1 > 0 && res2 < 0 ) || ( res1 < 0 && res2 > 0 ) || ( res1 == 0 && res2 == 0 && ItemStack.isSameItemSameTags(stack1, stack2) ) ) ) {
                        OhMyMinecraftClientReference.getLogger().error("Conflict result when comparing slot {} (name={}) with slot {} (name={}), result={} and {}",
                                                                           i, stack1.getItem().getName(stack1).toString(),
                                                                           j, stack2.getItem().getName(stack2).toString(),
                                                                           res1, res2);
                    }
                }
            }
            throw e;
        }

        // 倒序遍历来确保少的方块放在后面，多的方块放在前面
        for (int i = endSlot - 1; i >= startSlot; i--) {
            ItemStack dstStack = sortedItemStacks.get(i - startSlot);
            int dstIdx = -1;

            if (itemStacks.get(i) != dstStack) {
                for (int j = startSlot; j < endSlot; j++) {
                    if (itemStacks.get(j) == dstStack) {
                        dstIdx = j;
                        break;
                    }
                }

                if (dstIdx == -1) {
                    // wtf???
                    continue;
                }

                if (itemStacks.get(i).getCount() < dstStack.getCount()) {
                    ret.add(new Tuple<>(dstIdx, i));
                } else {
                    ret.add(new Tuple<>(i, dstIdx));
                }

                itemStacks.set(dstIdx, itemStacks.get(i));
                itemStacks.set(i, dstStack);
            }
        }

        return ret;
    }

    private static @NotNull List<Integer> mergeItems(@NotNull ItemStack cursorStack, List<ItemStack> targetItemStacks,
                                                     int beginSlot, int endSlot) {
        List<Integer> ret = Lists.newArrayList();

        // 先把手中的物品尽量的放入背包或容器中，从而保证后续的整理不会被手中物品合并而影响
        if (!cursorStack.isEmpty()) {
            ret.addAll(SortInventoryUtil.addItemStack(targetItemStacks, cursorStack, beginSlot, endSlot));
        }

        for (int i = endSlot - 1; i >= beginSlot; i--) {
            ItemStack stack = targetItemStacks.get(i);

            if (stack.isEmpty()) {
                continue;
            }

            targetItemStacks.set(i, new ItemStack(Blocks.AIR));
            List<Integer> addItemStackClickList = SortInventoryUtil.addItemStack(targetItemStacks, stack,
                    beginSlot, i + 1);

            if (!addItemStackClickList.isEmpty()) {
                ret.add(i);
                ret.addAll(addItemStackClickList);

                if (!stack.isEmpty()) {
                    ret.add(i);
                    targetItemStacks.set(i, stack);
                } else if (!cursorStack.isEmpty()) {
                    targetItemStacks.set(i, cursorStack);
                }
            } else {
                targetItemStacks.set(i, stack);
            }
        }

        // 在合并完后如果鼠标还有物品则尝试把鼠标的物品放进容器或箱子
        if (!cursorStack.isEmpty()) {
            for (int i = beginSlot; i < endSlot; i++) {
                if (targetItemStacks.get(i).isEmpty()) {
                    ret.add(i);
                    targetItemStacks.set(i, cursorStack.copy());
                    cursorStack.setCount(0);
                    break;
                }
            }
        }

        return ret;
    }

    private static boolean bothContains(String target, @NotNull String a, @NotNull String b) {
        return a.contains(target) && b.contains(target);
    }

    static class ItemStackComparator implements Comparator<ItemStack> {
        @Override
        public int compare(ItemStack a, ItemStack b) {
            int aId = SortInventoryUtil.getItemId(a);
            int bId = SortInventoryUtil.getItemId(b);

            if (Configs.sortInventoryShulkerBoxLast == Configs.SortInventoryShulkerBoxLastType.TRUE ||
                    (Configs.sortInventoryShulkerBoxLast == Configs.SortInventoryShulkerBoxLastType.AUTO &&
                            !allShulkerBox)) {
                if (ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && !ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
                    return 1;
                } else if (!ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && ShulkerBoxItemUtil.isShulkerBoxBlockItem(b)) {
                    return -1;
                }
            }

            if (ShulkerBoxItemUtil.isShulkerBoxBlockItem(a) && ShulkerBoxItemUtil.isShulkerBoxBlockItem(b) &&
                    a.getItem() == b.getItem()) {
                return -ShulkerBoxItemUtil.compareShulkerBox(a.getTag(), b.getTag());
            }

            if (a.getItem() instanceof BlockItem && b.getItem() instanceof BlockItem) {
                Block blockA = ((BlockItem) a.getItem()).getBlock();
                Block blockB = ((BlockItem) b.getItem()).getBlock();

                if (blockA instanceof IDyeBlock && blockB instanceof IDyeBlock && blockA.getClass() == blockB.getClass()) {
                    return SortInventoryUtil.DYE_COLOR_MAPPING.get(((IDyeBlock) blockA).ommc$getColor()) -
                            SortInventoryUtil.DYE_COLOR_MAPPING.get(((IDyeBlock) blockB).ommc$getColor());
                }

                //#if MC > 11902
                String ida = BuiltInRegistries.BLOCK.getKey(blockA).getPath();
                String idb = BuiltInRegistries.BLOCK.getKey(blockB).getPath();
                //#else
                //$$ String ida = Registry.BLOCK.getKey(blockA).getPath();
                //$$ String idb = Registry.BLOCK.getKey(blockB).getPath();
                //#endif

                if (SortInventoryUtil.bothContains("wool", ida, idb) ||
                        SortInventoryUtil.bothContains("carpet", ida, idb) ||
                        SortInventoryUtil.bothContains("bed", ida, idb) ||
                        SortInventoryUtil.bothContains("terracotta", ida, idb) ||
                        SortInventoryUtil.bothContains("concrete", ida, idb) ||
                        SortInventoryUtil.bothContains("candle", ida, idb)) {
                    return SortInventoryUtil.MAP_COLOR_MAPPING.getOrDefault(
                            //#if MC > 11502
                            blockA.defaultMapColor(),
                            //#else
                            //$$ ((AccessorBlock) blockA).getMaterialColor(),
                            //#endif
                            0
                    ) -
                            SortInventoryUtil.MAP_COLOR_MAPPING.getOrDefault(
                                    //#if MC > 11502
                                    blockB.defaultMapColor(),
                                    //#else
                                    //$$ ((AccessorBlock) blockB).getMaterialColor(),
                                    //#endif
                                    0
                            );
                }
            }

            if (a.getItem() instanceof DyeItem && b.getItem() instanceof DyeItem) {
                return SortInventoryUtil.DYE_COLOR_MAPPING.get(((DyeItem) a.getItem()).getDyeColor()) -
                        SortInventoryUtil.DYE_COLOR_MAPPING.get(((DyeItem) b.getItem()).getDyeColor());
            }

            if (a.isEmpty() && !b.isEmpty()) {
                return 1;
            } else if (!a.isEmpty() && b.isEmpty()) {
                return -1;
            } else if (a.isEmpty()) {
                return 0;
            }

            if (aId == bId) {
                // 有 nbt 标签的排在前面
                if (!a.hasTag() && b.hasTag()) {
                    return 1;
                } else if (a.hasTag() && !b.hasTag()) {
                    return -1;
                } else if (a.hasTag()) {
                    // 如果都有 nbt 的话，确保排序后相邻的物品 nbt 标签一致
                    return Objects.compare(a.getTag(), b.getTag(), Comparator.comparingInt(CompoundTag::hashCode));
                }

                // 物品少的排在后面
                return b.getCount() - a.getCount();
            }

            return aId - bId;
        }
    }
}
