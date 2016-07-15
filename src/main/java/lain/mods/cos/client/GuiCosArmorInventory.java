package lain.mods.cos.client;

import java.io.IOException;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import lain.mods.cos.network.packet.PacketSetSkinArmor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiCosArmorInventory extends InventoryEffectRenderer
{

    public static final ResourceLocation texture = new ResourceLocation("cosmeticarmorreworked", "textures/gui/cosarmorinventory.png");

    private float xSizeFloat;
    private float ySizeFloat;

    public GuiCosArmorInventory(Container container)
    {
        super(container);
        allowUserInput = true;
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            mc.displayGuiScreen(new GuiAchievements(this, mc.thePlayer.getStatFileWriter()));
        }
        if (button.id == 1)
            mc.displayGuiScreen(new GuiStats(this, mc.thePlayer.getStatFileWriter()));

        if (button.id >= 80 && button.id < 84)
        {
            int i = button.id - 80;
            InventoryCosArmor inv = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(mc.thePlayer.getUniqueID());
            inv.setSkinArmor(i, !inv.isSkinArmor(i));
            inv.markDirty();
            ((GuiCosArmorToggleButton) button).state = inv.isSkinArmor(i) ? 1 : 0;
            CosmeticArmorReworked.network.sendToServer(new PacketSetSkinArmor(mc.thePlayer, i));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_drawGuiContainerBackgroundLayer_1_, int p_drawGuiContainerBackgroundLayer_2_, int p_drawGuiContainerBackgroundLayer_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(texture);
        int i = guiLeft;
        int j = guiTop;
        drawTexturedModalRect(i, j, 0, 0, xSize, ySize);

        GuiInventory.drawEntityOnScreen(i + 51, j + 75, 30, i + 51 - xSizeFloat, j + 75 - 50 - ySizeFloat, mc.thePlayer);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_drawGuiContainerForegroundLayer_1_, int p_drawGuiContainerForegroundLayer_2_)
    {
        fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 97, 8, 4210752);
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_)
    {
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        xSizeFloat = p_drawScreen_1_;
        ySizeFloat = p_drawScreen_2_;
    }

    @Override
    public void initGui()
    {
        buttonList.clear();
        super.initGui();

        for (int i = 0; i < 4; i++)
        {
            int j = 3 - i;
            GuiCosArmorToggleButton t = new GuiCosArmorToggleButton(80 + j, guiLeft + 97 + 18 * i, guiTop + 56, 5, 5, "");
            t.state = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(mc.thePlayer.getUniqueID()).isSkinArmor(j) ? 1 : 0;
            buttonList.add(t);
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) throws IOException
    {
        if (par2 == CosmeticArmorReworked.keyHandler.keyOpenCosArmorInventory.getKeyCode())
            mc.thePlayer.closeScreen();
        else
            super.keyTyped(par1, par2);
    }

}
