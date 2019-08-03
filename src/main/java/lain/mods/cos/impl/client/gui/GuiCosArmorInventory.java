package lain.mods.cos.impl.client.gui;

import javax.annotation.Nullable;
import com.mojang.blaze3d.platform.GlStateManager;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.packet.PacketSetSkinArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GuiCosArmorInventory extends DisplayEffectsScreen<ContainerCosArmor> implements IRecipeShownListener
{

    public static final ResourceLocation TEXTURE = new ResourceLocation("cosmeticarmorreworked", "textures/gui/cosarmorinventory.png");
    public static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");

    protected Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);

    public float oldMouseX;
    public float oldMouseY;

    private final RecipeBookGui recipeBook = new RecipeBookGui();
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public GuiCosArmorInventory(ContainerCosArmor container, PlayerInventory invPlayer, ITextComponent displayName)
    {
        super(container, invPlayer, displayName);
        passEvents = true;

        smoothTransition();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        int i = guiLeft;
        int j = guiTop;
        blit(i, j, 0, 0, xSize, ySize);
        InventoryScreen.drawEntityOnScreen(i + 51, j + 75, 30, (float) (i + 51) - oldMouseX, (float) (j + 75 - 50) - oldMouseY, mc.player);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        font.drawString(I18n.format("container.crafting"), 97.0F, 8.0F, 4210752);
    }

    @Override
    public RecipeBookGui func_194310_f()
    {
        return recipeBook;
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
    protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_)
    {
        boolean flag = p_195361_1_ < (double) p_195361_5_ || p_195361_3_ < (double) p_195361_6_ || p_195361_1_ >= (double) (p_195361_5_ + xSize) || p_195361_3_ >= (double) (p_195361_6_ + ySize);
        return recipeBook.func_195604_a(p_195361_1_, p_195361_3_, guiLeft, guiTop, xSize, ySize, p_195361_7_) && flag;
    }

    @Override
    protected void init()
    {
        super.init();
        widthTooNarrow = width < 379;
        recipeBook.func_201520_a(width, height, mc, widthTooNarrow, (RecipeBookContainer<?>) container);
        guiLeft = recipeBook.updateScreenPosition(widthTooNarrow, width, xSize);
        children.add(recipeBook);
        addButton(new ImageButton(guiLeft + 76, guiTop + 27, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, button -> {
//            int lastLeft = guiLeft;
            recipeBook.func_201518_a(widthTooNarrow);
            recipeBook.toggleVisibility();
            guiLeft = recipeBook.updateScreenPosition(widthTooNarrow, width, xSize);
            ((ImageButton) button).setPosition(guiLeft + 76, guiTop + 27);
            buttonClicked = true;
//            int leftDiff = guiLeft - lastLeft;
//            buttons.stream().filter(IShiftingWidget.class::isInstance).forEach(b -> b.x += leftDiff);
        }));
        InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUniqueID());
        for (int i = 0; i < 4; i++)
        {
            int j = 3 - i;
            addButton(new GuiCosArmorToggleButton(guiLeft + 97 + 18 * i, guiTop + 61, 5, 5, "", invCosArmor.isSkinArmor(j) ? 1 : 0, button -> {
                InventoryCosArmor inv = ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUniqueID());
                inv.setSkinArmor(j, !inv.isSkinArmor(j));
                ((GuiCosArmorToggleButton) button).state = inv.isSkinArmor(j) ? 1 : 0;
                ModObjects.network.sendToServer(new PacketSetSkinArmor(j, inv.isSkinArmor(j)));
            }));
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
    public void recipesUpdated()
    {
        recipeBook.recipesUpdated();
    }

    @Override
    public void removed()
    {
        recipeBook.removed();

        super.removed();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        renderBackground();
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

    private void smoothTransition()
    {
        if (mc.currentScreen instanceof InventoryScreen)
        {
            oldMouseX = ((InventoryScreen) mc.currentScreen).oldMouseX;
            oldMouseY = ((InventoryScreen) mc.currentScreen).oldMouseY;
        }
    }

    @Override
    public void tick()
    {
        recipeBook.tick();
    }

}
