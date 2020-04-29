package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GuiCosArmorToggleButton extends Button implements IShiftingWidget
{

    protected Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
    public int state;
    public int stamp;

    public GuiCosArmorToggleButton(int x, int y, int width, int height, String message, int initialState, Button.IPressable onPress)
    {
        super(x, y, width, height, message, onPress);
        state = initialState;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(GuiCosArmorInventory.TEXTURE);
        RenderSystem.disableDepthTest();
        blit(x, y, 0 + 5 * state, 176, 5, 5);
        RenderSystem.enableDepthTest();
    }

    public GuiCosArmorToggleButton setStamp(int stamp)
    {
        this.stamp = stamp;
        return this;
    }

}
