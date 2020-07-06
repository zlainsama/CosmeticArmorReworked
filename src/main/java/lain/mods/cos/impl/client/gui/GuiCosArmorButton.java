package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GuiCosArmorButton extends Button implements IShiftingWidget
{

    protected Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);

    public GuiCosArmorButton(int x, int y, int width, int height, ITextComponent message, Button.IPressable onPress)
    {
        super(x, y, width, height, message, onPress);
    }

    @Override
    public void func_230431_b_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
    {
        mc.getTextureManager().bindTexture(GuiCosArmorInventory.TEXTURE);
        RenderSystem.disableDepthTest();
        if (func_230449_g_())
        {
            func_238474_b_(matrix, field_230690_l_, field_230691_m_, 10, 166, 10, 10);
            func_238472_a_(matrix, mc.fontRenderer, func_230458_i_(), field_230690_l_ + 5, field_230691_m_ + field_230689_k_, 0xffffff);
        }
        else
        {
            func_238474_b_(matrix, field_230690_l_, field_230691_m_, 0, 166, 10, 10);
        }
        RenderSystem.enableDepthTest();
    }

    @Override
    public void shiftLeft(int diffLeft)
    {
        field_230690_l_ += diffLeft;
    }

}
