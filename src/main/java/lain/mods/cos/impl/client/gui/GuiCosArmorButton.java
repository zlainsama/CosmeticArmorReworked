package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;

public class GuiCosArmorButton extends Button implements IShiftingWidget, ICreativeInvWidget {

    private final Minecraft mc = Minecraft.getInstance();
    private final BiConsumer<GuiCosArmorButton, Boolean> onCreativeTabChanged;

    public GuiCosArmorButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, BiConsumer<GuiCosArmorButton, Boolean> onCreativeTabChanged) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.onCreativeTabChanged = onCreativeTabChanged;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        boolean state = isHoveredOrFocused();
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        graphics.blit(GuiCosArmorInventory.TEXTURE, getX(), getY(), state ? 10 : 0, 166, 10, 10);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (state)
            graphics.drawCenteredString(mc.font, getMessage(), getX() + 5, getY() + height, 0xffffff);
    }

    @Override
    public void shiftLeft(int diffLeft) {
        setX(getX() + diffLeft);
    }

    @Override
    public void onSelectedTabChanged(boolean isInventoryOpen) {
        if (onCreativeTabChanged != null)
            onCreativeTabChanged.accept(this, isInventoryOpen);
    }
}
