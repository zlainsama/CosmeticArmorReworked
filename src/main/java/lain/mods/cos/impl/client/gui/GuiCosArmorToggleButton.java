package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GuiCosArmorToggleButton extends Button
{

    protected Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
    public int state;

    public GuiCosArmorToggleButton(int x, int y, int width, int height, String message, int initialState, Button.IPressable onPress)
    {
        super(x, y, width, height, message, onPress);
        state = initialState;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (visible)
        {
            mc.getTextureManager().bindTexture(GuiCosArmorInventory.TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            blit(x, y, 0 + 5 * state, 176, 5, 5);
            renderBg(mc, mouseX, mouseY);
        }
    }

}
