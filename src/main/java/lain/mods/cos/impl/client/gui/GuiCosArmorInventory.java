package lain.mods.cos.impl.client.gui;

import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSet;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.packet.PacketSetSkinArmor;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerRecipeBook;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class GuiCosArmorInventory extends InventoryEffectRenderer implements IRecipeShownListener
{

    public static final ResourceLocation TEXTURE = new ResourceLocation("cosmeticarmorreworked", "textures/gui/cosarmorinventory.png");
    public static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    public static final Set<Integer> ToggleButtonIds = ImmutableSet.of(80, 81, 82, 83);

    public float oldMouseX;
    public float oldMouseY;

    private final GuiRecipeBook recipeBook = new GuiRecipeBook();
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public GuiCosArmorInventory(ContainerCosArmor container)
    {
        super(container);
        allowUserInput = true;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        int i = guiLeft;
        int j = guiTop;
        drawTexturedModalRect(i, j, 0, 0, xSize, ySize);
        GuiInventory.drawEntityOnScreen(i + 51, j + 75, 30, (float) (i + 51) - oldMouseX, (float) (j + 75 - 50) - oldMouseY, mc.player);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString(I18n.format("container.crafting"), 97.0F, 8.0F, 4210752);
    }

    @Override
    public GuiRecipeBook func_194310_f()
    {
        return recipeBook;
    }

    @Override
    protected boolean func_195361_a(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_)
    {
        boolean flag = p_195361_1_ < (double) p_195361_5_ || p_195361_3_ < (double) p_195361_6_ || p_195361_1_ >= (double) (p_195361_5_ + xSize) || p_195361_3_ >= (double) (p_195361_6_ + ySize);
        return recipeBook.func_195604_a(p_195361_1_, p_195361_3_, guiLeft, guiTop, xSize, ySize, p_195361_7_) && flag;
    }

    @Override
    @Nullable
    public IGuiEventListener getFocused()
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
    protected void initGui()
    {
        super.initGui();
        widthTooNarrow = width < 379;
        recipeBook.func_201520_a(width, height, mc, widthTooNarrow, (ContainerRecipeBook) inventorySlots);
        guiLeft = recipeBook.updateScreenPosition(widthTooNarrow, width, xSize);
        children.add(recipeBook);
        addButton(new GuiButtonImage(10, guiLeft + 76, guiTop + 27, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE)
        {

            @Override
            public void onClick(double mouseX, double mouseY)
            {
                int lastLeft = GuiCosArmorInventory.this.guiLeft;
                GuiCosArmorInventory.this.recipeBook.func_201518_a(GuiCosArmorInventory.this.widthTooNarrow);
                GuiCosArmorInventory.this.recipeBook.toggleVisibility();
                GuiCosArmorInventory.this.guiLeft = GuiCosArmorInventory.this.recipeBook.updateScreenPosition(GuiCosArmorInventory.this.widthTooNarrow, GuiCosArmorInventory.this.width, GuiCosArmorInventory.this.xSize);
                setPosition(GuiCosArmorInventory.this.guiLeft + 104, GuiCosArmorInventory.this.height / 2 - 22);
                GuiCosArmorInventory.this.buttonClicked = true;
                int leftDiff = GuiCosArmorInventory.this.guiLeft - lastLeft;
                GuiCosArmorInventory.this.buttons.stream().filter(b -> GuiCosArmorInventory.ToggleButtonIds.contains(b.id)).forEach(b -> b.x += leftDiff);
            }

        });
        InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUniqueID());
        for (int i = 0; i < 4; i++)
        {
            int j = 3 - i;
            int id = 80 + j;
            addButton(new GuiCosArmorToggleButton(id, guiLeft + 97 + 18 * i, guiTop + 61, 5, 5, "", invCosArmor.isSkinArmor(j) ? 1 : 0)
            {

                @Override
                public void onClick(double mouseX, double mouseY)
                {
                    InventoryCosArmor inv = ModObjects.invMan.getCosArmorInventoryClient(GuiCosArmorInventory.this.mc.player.getUniqueID());
                    inv.setSkinArmor(j, !inv.isSkinArmor(j));
                    state = inv.isSkinArmor(j) ? 1 : 0;
                    ModObjects.network.sendToServer(new PacketSetSkinArmor(GuiCosArmorInventory.this.mc.player.getUniqueID(), j));
                }

            });
        }
    }

    @Override
    protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_)
    {
        return (!widthTooNarrow || !recipeBook.isVisible()) && super.isPointInRegion(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
    {
        if (recipeBook.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_))
        {
            return true;
        }
        else
        {
            return widthTooNarrow && recipeBook.isVisible() ? false : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        }
    }

    @Override
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_)
    {
        if (buttonClicked)
        {
            buttonClicked = false;
            return true;
        }
        else
        {
            return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
        }
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
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        hasActivePotionEffects = !recipeBook.isVisible();
        if (recipeBook.isVisible() && widthTooNarrow)
        {
            drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
            recipeBook.render(mouseX, mouseY, partialTicks);
        }
        else
        {
            recipeBook.render(mouseX, mouseY, partialTicks);
            super.render(mouseX, mouseY, partialTicks);
            recipeBook.renderGhostRecipe(guiLeft, guiTop, false, partialTicks);
        }

        renderHoveredToolTip(mouseX, mouseY);
        recipeBook.renderTooltip(guiLeft, guiTop, mouseX, mouseY);
        oldMouseX = (float) mouseX;
        oldMouseY = (float) mouseY;
    }

    @Override
    public void tick()
    {
        recipeBook.tick();
    }

}
