package me.aleksilassila.litematica.printer.mixin.printer.mc;

import me.aleksilassila.litematica.printer.render.Render2D;
import me.aleksilassila.litematica.printer.render.HudVisibilityPolicy;
import me.aleksilassila.litematica.printer.utils.render.Render2DUtils;
import me.aleksilassila.litematica.printer.config.Configs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public abstract class MixinGui {
    // @formatter:off
    @Inject(method = "render", at = @At("TAIL"))

    private void hookRenderHud(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.player.isSpectator()
                || !HudVisibilityPolicy.shouldRender(
                        Configs.Core.WORK_SWITCH.getBooleanValue(),
                        Configs.Core.RENDER_HUD.getBooleanValue(),
                        Configs.Core.MISSING_MATERIAL_HUD.getBooleanValue()
                )) {
            return;
        }

        // 初始化渲染矩阵
        Render2DUtils.initGuiGraphics(guiGraphics);

        float scaledWidth = mc.getWindow().getGuiScaledWidth();
        float scaledHeight = mc.getWindow().getGuiScaledHeight();
        Render2D.INSTANCE.render(scaledWidth, scaledHeight);
    }
    // @formatter:on
}
