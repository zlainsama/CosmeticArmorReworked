package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GuiCosArmorButton extends Button implements IShiftingWidget {

    protected Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);

    public GuiCosArmorButton(int x, int y, int width, int height, ITextComponent message, Button.IPressable onPress) {
        super(x, y, width, height, message, onPress);
    }

    @Override
    public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        mc.getTextureManager().bindTexture(GuiCosArmorInventory.TEXTURE);
        RenderSystem.disableDepthTest();
        if (isHovered()) {
            blit(matrix, x, y, 10, 166, 10, 10);
            drawCenteredString(matrix, mc.fontRenderer, getMessage(), x + 5, y + height, 0xffffff);
        } else {
            blit(matrix, x, y, 0, 166, 10, 10);
        }
        RenderSystem.enableDepthTest();
    }

    @Override
    public void shiftLeft(int diffLeft) {
        x += diffLeft;
    }

}
