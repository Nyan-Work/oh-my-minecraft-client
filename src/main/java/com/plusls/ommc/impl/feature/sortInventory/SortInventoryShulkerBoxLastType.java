package com.plusls.ommc.impl.feature.sortInventory;

import com.plusls.ommc.SharedConstants;
import top.hendrixshen.magiclib.api.malilib.config.option.EnumOptionEntry;

public enum SortInventoryShulkerBoxLastType implements EnumOptionEntry {
    AUTO,
    ENABLE,
    DISABLE;

    @Override
    public EnumOptionEntry[] getAllValues() {
        return SortInventoryShulkerBoxLastType.values();
    }

    @Override
    public EnumOptionEntry getDefault() {
        return null;
    }

    @Override
    public String getTranslationPrefix() {
        return String.format("%s.config.option.sortInventoryShulkerBoxLast", SharedConstants.getModIdentifier());
    }
}
