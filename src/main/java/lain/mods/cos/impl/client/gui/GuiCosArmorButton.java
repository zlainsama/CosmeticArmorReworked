package lain.mods.cos.impl.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GuiCosArmorButton extends GuiButton
{

    protected Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);

    public GuiCosArmorButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText)
    {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (visible)
        {
            mc.getTextureManager().bindTexture(GuiCosArmorInventory.TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            if (hovered)
            {
                drawTexturedModalRect(x, y, 10, 166, 10, 10);
                drawCenteredString(mc.fontRenderer, I18n.format(displayString), x + 5, y + height, 0xffffff);
            }
            else
            {
                drawTexturedModalRect(x, y, 0, 166, 10, 10);
            }

            renderBg(mc, mouseX, mouseY);
        }
    }

}
