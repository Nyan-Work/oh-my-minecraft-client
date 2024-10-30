package com.plusls.ommc.mixin.accessor;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.commands.HelpCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The implementation for mc [1.14.4, ~)
 * <p>
 * Code from <a href="https://github.com/FabricMC/fabric/blob/1.17/fabric-command-api-v1/src/main/java/net/fabricmc/fabric/mixin/command/HelpCommandAccessor.java">Fabric Command API v1</a>
 */
@Mixin(HelpCommand.class)
public interface AccessorHelpCommand {
    @Accessor("ERROR_FAILED")
    static SimpleCommandExceptionType getFailedException() {
        throw new AssertionError("mixin");
    }
}
