package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GuiCosArmorToggleButton extends Button implements IShiftingWidget
{

    protected Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
    public int state;
    public int stamp;

    public GuiCosArmorToggleButton(int x, int y, int width, int height, ITextComponent message, int initialState, Button.IPressable onPress)
    {
        super(x, y, width, height, message, onPress);
        state = initialState;
    }

    @Override
    public void func_230431_b_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(GuiCosArmorInventory.TEXTURE);
        RenderSystem.disableDepthTest();
        func_238474_b_(matrix, field_230690_l_, field_230691_m_, 0 + 5 * state, 176, 5, 5);
        RenderSystem.enableDepthTest();
    }

    public GuiCosArmorToggleButton setStamp(int stamp)
    {
        this.stamp = stamp;
        return this;
    }

    @Override
    public void shiftLeft(int diffLeft)
    {
        field_230690_l_ += diffLeft;
    }

}
