package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.packet.PacketSetSkinArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GuiCosArmorInventory extends DisplayEffectsScreen<ContainerCosArmor> implements IRecipeShownListener
{

    public static final ResourceLocation TEXTURE = new ResourceLocation("cosmeticarmorreworked", "textures/gui/cosarmorinventory.png");
    public static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");

    protected Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);

    public float oldMouseX;
    public float oldMouseY;

    private ITextComponent craftingText;

    private final RecipeBookGui recipeBook = new RecipeBookGui()
    {

        @Override
        public boolean isVisible()
        {
            return super.isVisible() && !ModConfigs.CosArmorDisableRecipeBook.get();
        }

        @Override
        public void toggleVisibility()
        {
            setVisible(!super.isVisible());
        }

    };

    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public GuiCosArmorInventory(ContainerCosArmor container, PlayerInventory invPlayer, ITextComponent displayName)
    {
        super(container, invPlayer, displayName);
        field_230711_n_ = true;
        field_238742_p_ = 97;

        craftingText = new TranslationTextComponent("container.crafting");

        smoothTransition();
    }

    @Override
    public void func_230430_a_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
    {
        func_230446_a_(matrix);
        hasActivePotionEffects = !recipeBook.isVisible();
        if (recipeBook.isVisible() && widthTooNarrow)
        {
            func_230450_a_(matrix, partialTicks, mouseX, mouseY);
            recipeBook.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
        }
        else
        {
            recipeBook.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
            super.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
            recipeBook.func_230477_a_(matrix, guiLeft, guiTop, false, partialTicks);
        }

        func_230459_a_(matrix, mouseX, mouseY);
        recipeBook.func_238924_c_(matrix, guiLeft, guiTop, mouseX, mouseY);
        oldMouseX = (float) mouseX;
        oldMouseY = (float) mouseY;
    }

    @Override
    protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        int i = guiLeft;
        int j = guiTop;
        func_238474_b_(matrix, i, j, 0, 0, xSize, ySize);
        InventoryScreen.drawEntityOnScreen(i + 51, j + 75, 30, (float) (i + 51) - oldMouseX, (float) (j + 75 - 50) - oldMouseY, mc.player);
    }

    @Override
    protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY)
    {
        field_230712_o_.func_243248_b(matrix, craftingText, (float) field_238742_p_, (float) field_238743_q_, 4210752);
    }

    @Override
    public void func_231023_e_()
    {
        recipeBook.tick();
    }

    @Override
    public boolean func_231044_a_(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
    {
        if (recipeBook.func_231044_a_(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_))
        {
            return true;
        }
        else
        {
            return widthTooNarrow && recipeBook.isVisible() ? false : super.func_231044_a_(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        }
    }

    @Override
    public boolean func_231048_c_(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_)
    {
        if (buttonClicked)
        {
            buttonClicked = false;
            return true;
        }
        else
        {
            return super.func_231048_c_(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
        }
    }

    @Override
    protected void func_231160_c_()
    {
        super.func_231160_c_();
        widthTooNarrow = field_230708_k_ < 379;
        recipeBook.init(field_230708_k_, field_230709_l_, mc, widthTooNarrow, (RecipeBookContainer<?>) container);
        guiLeft = recipeBook.updateScreenPosition(widthTooNarrow, field_230708_k_, xSize);
        field_230705_e_.add(recipeBook);
        setFocusedDefault(recipeBook);
        if (!ModConfigs.CosArmorDisableRecipeBook.get())
        {
            func_230480_a_(new ImageButton(guiLeft + 76, guiTop + 27, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, button -> {
//            int lastLeft = guiLeft;
                recipeBook.initSearchBar(widthTooNarrow);
                recipeBook.toggleVisibility();
                guiLeft = recipeBook.updateScreenPosition(widthTooNarrow, field_230708_k_, xSize);
                ((ImageButton) button).setPosition(guiLeft + 76, guiTop + 27);
                buttonClicked = true;
//            int leftDiff = guiLeft - lastLeft;
//            buttons.stream().filter(IShiftingWidget.class::isInstance).forEach(b -> b.x += leftDiff);
            }));
        }
        InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUniqueID());
        for (int i = 0; i < 4; i++)
        {
            int j = 3 - i;
            func_230480_a_(new GuiCosArmorToggleButton(guiLeft + 97 + 18 * i, guiTop + 61, 5, 5, new StringTextComponent(""), invCosArmor.isSkinArmor(j) ? 1 : 0, button -> {
                InventoryCosArmor inv = ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUniqueID());
                inv.setSkinArmor(j, !inv.isSkinArmor(j));
                ((GuiCosArmorToggleButton) button).state = inv.isSkinArmor(j) ? 1 : 0;
                ModObjects.network.sendToServer(new PacketSetSkinArmor(j, inv.isSkinArmor(j)));
            }));
        }
    }

    @Override
    public void func_231164_f_()
    {
        recipeBook.removed();

        super.func_231164_f_();
    }

    @Override
    public RecipeBookGui getRecipeGui()
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
    protected boolean isPointInRegion(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_)
    {
        return (!widthTooNarrow || !recipeBook.isVisible()) && super.isPointInRegion(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
    }

    @Override
    public void recipesUpdated()
    {
        recipeBook.recipesUpdated();
    }

    private void smoothTransition()
    {
        if (mc.currentScreen instanceof InventoryScreen)
        {
            oldMouseX = ((InventoryScreen) mc.currentScreen).oldMouseX;
            oldMouseY = ((InventoryScreen) mc.currentScreen).oldMouseY;
        }
    }

}
