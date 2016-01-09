package lain.mods.cos.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

public class GuiCosArmorButton extends GuiButton
{

    public GuiCosArmorButton(int arg0, int arg1, int arg2, int arg3, int arg4, String arg5)
    {
        super(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void drawButton(Minecraft mc, int x, int y)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(GuiCosArmorInventory.texture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            hovered = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
            int state = this.getHoverState(hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            if (state == 1)
            {
                drawTexturedModalRect(this.xPosition, this.yPosition, 0, 166, 10, 10);
            }
            else
            {
                drawTexturedModalRect(this.xPosition, this.yPosition, 10, 166, 10, 10);
                drawCenteredString(fontrenderer, I18n.format(this.displayString), this.xPosition + 5, this.yPosition + this.height, 0xffffff);
            }

            mouseDragged(mc, x, y);
        }
    }

}
