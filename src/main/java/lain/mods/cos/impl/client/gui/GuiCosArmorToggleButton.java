package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GuiCosArmorToggleButton extends Button implements IShiftingWidget {

    public int state;

    protected Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);

    public GuiCosArmorToggleButton(int x, int y, int width, int height, ITextComponent message, int initialState, Button.IPressable onPress) {
        super(x, y, width, height, message, onPress);
        state = initialState;
    }

    @Override
    public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        mc.getTextureManager().bindTexture(GuiCosArmorInventory.TEXTURE);
        RenderSystem.disableDepthTest();
        blit(matrix, x, y, 0 + 5 * state, 176, 5, 5);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void shiftLeft(int diffLeft) {
        x += diffLeft;
    }

}
