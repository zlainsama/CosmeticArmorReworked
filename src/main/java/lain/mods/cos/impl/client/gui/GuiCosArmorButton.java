package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import java.util.function.ObjIntConsumer;

public class GuiCosArmorButton extends Button implements IShiftingWidget, ICreativeInvWidget {

    private final Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
    private final ObjIntConsumer<GuiCosArmorButton> onCreativeTabChanged;

    public GuiCosArmorButton(int x, int y, int width, int height, ITextComponent message, Button.IPressable onPress, ObjIntConsumer<GuiCosArmorButton> onCreativeTabChanged) {
        super(x, y, width, height, message, onPress);
        this.onCreativeTabChanged = onCreativeTabChanged;
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

    @Override
    public void onSelectedTabChanged(int newTabIndex) {
        if (onCreativeTabChanged != null)
            onCreativeTabChanged.accept(this, newTabIndex);
    }
}
