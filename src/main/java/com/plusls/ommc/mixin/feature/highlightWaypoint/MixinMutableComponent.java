package com.plusls.ommc.mixin.feature.highlightWaypoint;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

//#if MC > 12002
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
//#else
//$$ import top.hendrixshen.magiclib.compat.preprocess.api.DummyClass;
//#endif

//#if MC > 12002
@Mixin(MutableComponent.class)
//#else
//$$ @Mixin(DummyClass.class)
//#endif
public class MixinMutableComponent {

    //#if MC > 12002
    @Final
    @Mutable
    @Shadow
    private List<Component> siblings;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void makeMutable(ComponentContents componentContents, List<Component> list, Style style, CallbackInfo ci) {
        if (list.getClass().getName().contains("ListN")) {
            siblings = new ArrayList<>(list);
        }
    }
    //#endif
}
