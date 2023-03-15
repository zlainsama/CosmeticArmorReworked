package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.BiConsumer;

public class GuiCosArmorButton extends Button implements IShiftingWidget, ICreativeInvWidget {

    private final Minecraft mc = Minecraft.getInstance();
    private final BiConsumer<GuiCosArmorButton, CreativeModeTab> onCreativeTabChanged;

    public GuiCosArmorButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, BiConsumer<GuiCosArmorButton, CreativeModeTab> onCreativeTabChanged) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.onCreativeTabChanged = onCreativeTabChanged;
    }

    @Override
    public void renderWidget(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GuiCosArmorInventory.TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.disableDepthTest();
        if (isHoveredOrFocused()) {
            blit(matrix, getX(), getY(), 10, 166, 10, 10);
            drawCenteredString(matrix, mc.font, getMessage(), getX() + 5, getY() + height, 0xffffff);
        } else {
            blit(matrix, getX(), getY(), 0, 166, 10, 10);
        }
        RenderSystem.enableDepthTest();
    }

    @Override
    public void shiftLeft(int diffLeft) {
        setX(getX() + diffLeft);
    }

    @Override
    public void onSelectedTabChanged(CreativeModeTab newTab) {
        if (onCreativeTabChanged != null)
            onCreativeTabChanged.accept(this, newTab);
    }
}
