package lain.mods.cos.client;

import java.io.IOException;
import java.util.Map;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.ContainerCosArmor;
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
    private boolean widthTooNarrow;
    private boolean buttonClicked;

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
            recipeBook.initVisuals(widthTooNarrow, ((ContainerCosArmor) inventorySlots).craftMatrix);
            recipeBook.toggleVisibility();
            guiLeft = recipeBook.updateScreenPosition(widthTooNarrow, width, xSize);
            recipeBookButton.setPosition(guiLeft + 76, guiTop + 27);

            for (int i = 0; i < 4; i++)
            {
                GuiCosArmorToggleButton t = toggleButtons.get(i);
                if (t == null)
                    continue;
                t.x = guiLeft + 97 + 18 * i;
                t.y = guiTop + 61;
            }

            buttonClicked = true;
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
        fontRenderer.drawString(I18n.format("container.crafting", new Object[0]), 97, 8, 4210752);
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_)
    {
        drawDefaultBackground();

        hasActivePotionEffects = !recipeBook.isVisible();

        if (recipeBook.isVisible() && widthTooNarrow)
        {
            drawGuiContainerBackgroundLayer(p_drawScreen_3_, p_drawScreen_1_, p_drawScreen_2_);
            recipeBook.render(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        }
        else
        {
            recipeBook.render(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
            super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
            recipeBook.renderGhostRecipe(guiLeft, guiTop, false, p_drawScreen_3_);
        }

        renderHoveredToolTip(p_drawScreen_1_, p_drawScreen_2_);
        recipeBook.renderTooltip(guiLeft, guiTop, p_drawScreen_1_, p_drawScreen_2_);

        oldMouseX = p_drawScreen_1_;
        oldMouseY = p_drawScreen_2_;
    }

    @Override
    public GuiRecipeBook func_194310_f()
    {
        return recipeBook;
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        recipeBook.slotClicked(slotIn);
    }

    @Override
    protected boolean hasClickedOutside(int p_193983_1_, int p_193983_2_, int p_193983_3_, int p_193983_4_)
    {
        boolean flag = p_193983_1_ < p_193983_3_ || p_193983_2_ < p_193983_4_ || p_193983_1_ >= p_193983_3_ + xSize || p_193983_2_ >= p_193983_4_ + ySize;
        return recipeBook.hasClickedOutside(p_193983_1_, p_193983_2_, guiLeft, guiTop, xSize, ySize) && flag;
    }

    @Override
    public void initGui()
    {
        buttonList.clear();
        super.initGui();

        widthTooNarrow = width < 379;
        recipeBook.func_194303_a(width, height, mc, widthTooNarrow, ((ContainerCosArmor) inventorySlots).craftMatrix);
        guiLeft = recipeBook.updateScreenPosition(widthTooNarrow, width, xSize);

        recipeBookButton = new GuiButtonImage(10, guiLeft + 76, guiTop + 27, 20, 18, 178, 0, 19, INVENTORY_BACKGROUND);
        buttonList.add(recipeBookButton);

        toggleButtons.clear();
        for (int i = 0; i < 4; i++)
        {
            int j = 3 - i;
            GuiCosArmorToggleButton t = new GuiCosArmorToggleButton(80 + j, guiLeft + 97 + 18 * i, guiTop + 61, 5, 5, "");
            t.state = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(mc.player.getUniqueID()).isSkinArmor(j) ? 1 : 0;
            buttonList.add(t);
            toggleButtons.put(i, t);
        }
    }

    @Override
    protected boolean isPointInRegion(int p_isPointInRegion_1_, int p_isPointInRegion_2_, int p_isPointInRegion_3_, int p_isPointInRegion_4_, int p_isPointInRegion_5_, int p_isPointInRegion_6_)
    {
        return (!widthTooNarrow || !recipeBook.isVisible()) && super.isPointInRegion(p_isPointInRegion_1_, p_isPointInRegion_2_, p_isPointInRegion_3_, p_isPointInRegion_4_, p_isPointInRegion_5_, p_isPointInRegion_6_);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!recipeBook.keyPressed(typedChar, keyCode))
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
        if (!recipeBook.mouseClicked(x, y, button))
            if (!widthTooNarrow || recipeBook.isVisible())
                super.mouseClicked(x, y, button);
    }

    @Override
    protected void mouseReleased(int x, int y, int state)
    {
        if (buttonClicked)
            buttonClicked = false;
        else
            super.mouseReleased(x, y, state);
    }

    @Override
    public void onGuiClosed()
    {
        recipeBook.removed();
        super.onGuiClosed();
    }

    @Override
    public void recipesUpdated()
    {
        recipeBook.recipesUpdated();
    }

    @Override
    public void updateScreen()
    {
        recipeBook.tick();
    }

}
