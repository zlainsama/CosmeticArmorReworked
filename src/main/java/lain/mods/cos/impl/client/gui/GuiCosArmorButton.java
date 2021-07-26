package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;

import java.util.function.ObjIntConsumer;

public class GuiCosArmorButton extends Button implements IShiftingWidget, ICreativeInvWidget {

    private final Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
    private final ObjIntConsumer<GuiCosArmorButton> onCreativeTabChanged;

    public GuiCosArmorButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, ObjIntConsumer<GuiCosArmorButton> onCreativeTabChanged) {
        super(x, y, width, height, message, onPress);
        this.onCreativeTabChanged = onCreativeTabChanged;
    }

    @Override
    public void renderButton(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, GuiCosArmorInventory.TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.disableDepthTest();
        if (isHovered()) {
            blit(matrix, x, y, 10, 166, 10, 10);
            drawCenteredString(matrix, mc.font, getMessage(), x + 5, y + height, 0xffffff);
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
