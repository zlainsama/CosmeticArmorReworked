package lain.mods.cos.impl.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

public class GuiCosArmorToggleButton extends Button implements IShiftingWidget {

    public int state;

    public GuiCosArmorToggleButton(int x, int y, int width, int height, Component message, int initialState, Button.OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        state = initialState;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(RenderType::guiTextured, GuiCosArmorInventory.TEXTURE, getX(), getY(), 0 + 5 * state, 176, 5, 5, 256, 256);
    }

    @Override
    public void shiftLeft(int diffLeft) {
        setX(getX() + diffLeft);
    }

}
