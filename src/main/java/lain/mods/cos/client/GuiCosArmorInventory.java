package lain.mods.cos.client;

import java.io.IOException;
import java.util.Map;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.ContainerCosArmor;
import lain.mods.cos.inventory.InventoryCosArmor;
import lain.mods.cos.network.packet.PacketSetSkinArmor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import com.google.common.collect.Maps;

public class GuiCosArmorInventory extends InventoryEffectRenderer implements IRecipeShownListener
{

    public static final ResourceLocation texture = new ResourceLocation("cosmeticarmorreworked", "textures/gui/cosarmorinventory.png");

    private float oldMouseX;
    private float oldMouseY;
    private GuiButtonImage recipeBookButton;
    private final GuiRecipeBook recipeBook = new GuiRecipeBook();
    private boolean flag1;
    private boolean mouseHit;

    private Map<Integer, GuiCosArmorToggleButton> toggleButtons = Maps.newHashMapWithExpectedSize(4);

    public GuiCosArmorInventory(Container container)
    {
        super(container);
        allowUserInput = true;
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 10)
        {
            recipeBook.func_193014_a(flag1, ((ContainerCosArmor) inventorySlots).craftMatrix);
            recipeBook.func_191866_a();
            guiLeft = recipeBook.func_193011_a(flag1, width, xSize);
            recipeBookButton.func_191746_c(guiLeft + 76, guiTop + 27);

            for (int i = 0; i < 4; i++)
            {
                GuiCosArmorToggleButton t = toggleButtons.get(i);
                if (t == null)
                    continue;
                t.xPosition = guiLeft + 97 + 18 * i;
                t.yPosition = guiTop + 56;
            }

            mouseHit = true;
        }
        else if (button.id >= 80 && button.id < 84)
        {
            int i = button.id - 80;
            InventoryCosArmor inv = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(mc.player.getUniqueID());
            inv.setSkinArmor(i, !inv.isSkinArmor(i));
            inv.markDirty();
            ((GuiCosArmorToggleButton) button).state = inv.isSkinArmor(i) ? 1 : 0;
            CosmeticArmorReworked.network.sendToServer(new PacketSetSkinArmor(mc.player, i));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_drawGuiContainerBackgroundLayer_1_, int p_drawGuiContainerBackgroundLayer_2_, int p_drawGuiContainerBackgroundLayer_3_)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(texture);
        int i = guiLeft;
        int j = guiTop;
        drawTexturedModalRect(i, j, 0, 0, xSize, ySize);

        GuiInventory.drawEntityOnScreen(i + 51, j + 75, 30, i + 51 - oldMouseX, j + 75 - 50 - oldMouseY, mc.player);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_drawGuiContainerForegroundLayer_1_, int p_drawGuiContainerForegroundLayer_2_)
    {
        fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 97, 8, 4210752);
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_)
    {
        drawDefaultBackground();

        hasActivePotionEffects = !recipeBook.func_191878_b();

        if (recipeBook.func_191878_b() && flag1)
        {
            drawGuiContainerBackgroundLayer(p_drawScreen_3_, p_drawScreen_1_, p_drawScreen_2_);
            recipeBook.func_191861_a(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        }
        else
        {
            recipeBook.func_191861_a(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
            super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
            recipeBook.func_191864_a(guiLeft, guiTop, false, p_drawScreen_3_);
        }

        func_191948_b(p_drawScreen_1_, p_drawScreen_2_);
        recipeBook.func_191876_c(guiLeft, guiTop, p_drawScreen_1_, p_drawScreen_2_);

        oldMouseX = p_drawScreen_1_;
        oldMouseY = p_drawScreen_2_;
    }

    @Override
    public void func_192043_J_()
    {
        recipeBook.func_193948_e();
    }

    @Override
    protected boolean func_193983_c(int p_193983_1_, int p_193983_2_, int p_193983_3_, int p_193983_4_)
    {
        boolean flag = p_193983_1_ < p_193983_3_ || p_193983_2_ < p_193983_4_ || p_193983_1_ >= p_193983_3_ + xSize || p_193983_2_ >= p_193983_4_ + ySize;
        return recipeBook.func_193955_c(p_193983_1_, p_193983_2_, guiLeft, guiTop, xSize, ySize) && flag;
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        recipeBook.func_191874_a(slotIn);
    }

    @Override
    public void initGui()
    {
        buttonList.clear();
        super.initGui();

        flag1 = width < 379;
        recipeBook.func_191856_a(width, height, mc, flag1, inventorySlots, ((ContainerCosArmor) inventorySlots).craftMatrix);
        guiLeft = recipeBook.func_193011_a(flag1, width, xSize);

        recipeBookButton = new GuiButtonImage(10, guiLeft + 76, guiTop + 27, 20, 18, 178, 0, 19, INVENTORY_BACKGROUND);
        buttonList.add(recipeBookButton);

        toggleButtons.clear();
        for (int i = 0; i < 4; i++)
        {
            int j = 3 - i;
            GuiCosArmorToggleButton t = new GuiCosArmorToggleButton(80 + j, guiLeft + 97 + 18 * i, guiTop + 56, 5, 5, "");
            t.state = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(mc.player.getUniqueID()).isSkinArmor(j) ? 1 : 0;
            buttonList.add(t);
            toggleButtons.put(i, t);
        }
    }

    @Override
    protected boolean isPointInRegion(int p_isPointInRegion_1_, int p_isPointInRegion_2_, int p_isPointInRegion_3_, int p_isPointInRegion_4_, int p_isPointInRegion_5_, int p_isPointInRegion_6_)
    {
        return (!flag1 || !recipeBook.func_191878_b()) && super.isPointInRegion(p_isPointInRegion_1_, p_isPointInRegion_2_, p_isPointInRegion_3_, p_isPointInRegion_4_, p_isPointInRegion_5_, p_isPointInRegion_6_);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!recipeBook.func_191859_a(typedChar, keyCode))
        {
            if (keyCode == CosmeticArmorReworked.keyHandler.keyOpenCosArmorInventory.getKeyCode())
                mc.player.closeScreen();
            else
                super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException
    {
        if (!recipeBook.func_191862_a(x, y, button))
            if (!flag1 || recipeBook.func_191878_b())
                super.mouseClicked(x, y, button);
    }

    @Override
    protected void mouseReleased(int x, int y, int state)
    {
        if (mouseHit)
            mouseHit = false;
        else
            super.mouseReleased(x, y, state);
    }

    @Override
    public void onGuiClosed()
    {
        recipeBook.func_191871_c();
        super.onGuiClosed();
    }

    @Override
    public void updateScreen()
    {
        recipeBook.func_193957_d();
    }

}
