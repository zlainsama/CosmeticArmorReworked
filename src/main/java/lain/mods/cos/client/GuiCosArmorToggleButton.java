package lain.mods.cos.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GuiCosArmorToggleButton extends GuiButton
{

    public int state = 0;

    public GuiCosArmorToggleButton(int arg0, int arg1, int arg2, int arg3, int arg4, String arg5)
    {
        super(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    @Override
    public void func_191745_a(Minecraft mc, int x, int y, float f)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(GuiCosArmorInventory.texture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            hovered = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0 + 5 * state, 176, 5, 5);

            this.mouseDragged(mc, x, y);
        }
    }

}
