package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class GuiCosArmorToggleButton extends Button implements IShiftingWidget {

    public int state;

    public GuiCosArmorToggleButton(int x, int y, int width, int height, Component message, int initialState, Button.OnPress onPress) {
        super(x, y, width, height, message, onPress);
        state = initialState;
    }

    @Override
    public void renderButton(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GuiCosArmorInventory.TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.disableDepthTest();
        blit(matrix, x, y, 0 + 5 * state, 176, 5, 5);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void shiftLeft(int diffLeft) {
        x += diffLeft;
    }

}
