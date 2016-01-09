package lain.mods.cos.client;

import java.io.IOException;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.PlayerUtils;
import lain.mods.cos.inventory.InventoryCosArmor;
import lain.mods.cos.network.packet.PacketSetSkinArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiCosArmorInventory extends InventoryEffectRenderer
{

    public static void drawPlayerModel(int p_147046_0_, int p_147046_1_, int p_147046_2_, float p_147046_3_, float p_147046_4_, EntityLivingBase p_147046_5_)
    {
        GL11.glEnable(2903);

        GL11.glPushMatrix();
        GL11.glTranslatef(p_147046_0_, p_147046_1_, 50.0F);
        GL11.glScalef(-p_147046_2_, p_147046_2_, p_147046_2_);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);

        float f1 = p_147046_5_.renderYawOffset;
        float f2 = p_147046_5_.rotationYaw;
        float f3 = p_147046_5_.rotationPitch;
        float f4 = p_147046_5_.prevRotationYawHead;
        float f5 = p_147046_5_.rotationYawHead;

        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);

        GL11.glRotatef(-(float) Math.atan(p_147046_4_ / 40.0F) * 20.0F, 1.0F, 0.0F, 0.0F);

        p_147046_5_.renderYawOffset = ((float) Math.atan(p_147046_3_ / 40.0F) * 20.0F);
        p_147046_5_.rotationYaw = ((float) Math.atan(p_147046_3_ / 40.0F) * 40.0F);
        p_147046_5_.rotationPitch = (-(float) Math.atan(p_147046_4_ / 40.0F) * 20.0F);
        p_147046_5_.rotationYawHead = p_147046_5_.rotationYaw;
        p_147046_5_.prevRotationYawHead = p_147046_5_.rotationYaw;

        GL11.glTranslated(0.0D, p_147046_5_.getYOffset(), 0.0D);
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        rm.playerViewY = 180.0F;
        rm.renderEntityWithPosYaw(p_147046_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);

        p_147046_5_.renderYawOffset = f1;
        p_147046_5_.rotationYaw = f2;
        p_147046_5_.rotationPitch = f3;
        p_147046_5_.prevRotationYawHead = f4;
        p_147046_5_.rotationYawHead = f5;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(32826);

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(3553);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

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
            InventoryCosArmor inv = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(PlayerUtils.getPlayerID(mc.thePlayer));
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

        drawPlayerModel(i + 51, j + 75, 30, i + 51 - xSizeFloat, j + 75 - 50 - ySizeFloat, mc.thePlayer);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_drawGuiContainerForegroundLayer_1_, int p_drawGuiContainerForegroundLayer_2_)
    {
        fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 106, 16, 4210752);
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

        int offset = 0;
        if (!mc.thePlayer.getActivePotionEffects().isEmpty() && !GuiEvents.isNeiHidden())
            offset -= 60;

        for (int i = 0; i < 4; i++)
        {
            int j = 3 - i;
            GuiCosArmorToggleButton t = new GuiCosArmorToggleButton(80 + j, guiLeft + 97 + offset, guiTop + 7 + 18 * i, 5, 5, "");
            t.state = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(PlayerUtils.getPlayerID(mc.thePlayer)).isSkinArmor(j) ? 1 : 0;
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
