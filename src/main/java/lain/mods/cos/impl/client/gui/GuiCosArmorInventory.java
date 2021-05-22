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
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GuiCosArmorInventory extends DisplayEffectsScreen<ContainerCosArmor> implements IRecipeShownListener {

    public static final ResourceLocation TEXTURE = new ResourceLocation("cosmeticarmorreworked", "textures/gui/cosarmorinventory.png");
    public static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");

    private final RecipeBookGui recipeBook = new RecipeBookGui() {

        @Override
        public boolean isVisible() {
            return super.isVisible() && !ModConfigs.CosArmorDisableRecipeBook.get();
        }

        @Override
        public void toggleVisibility() {
            setVisible(!super.isVisible());
        }

    };
    private final ITextComponent craftingText;
    public float oldMouseX;
    public float oldMouseY;
    protected Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
    private boolean useMousePos;
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public GuiCosArmorInventory(ContainerCosArmor container, PlayerInventory invPlayer, ITextComponent displayName) {
        super(container, invPlayer, displayName);
        passEvents = true;
        titleLabelX = 97;

        craftingText = new TranslationTextComponent("container.crafting");

        smoothTransition();
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrix);
        doRenderEffects = !recipeBook.isVisible();
        if (recipeBook.isVisible() && widthTooNarrow) {
            renderBg(matrix, partialTicks, mouseX, mouseY);
            recipeBook.render(matrix, mouseX, mouseY, partialTicks);
        } else {
            recipeBook.render(matrix, mouseX, mouseY, partialTicks);
            super.render(matrix, mouseX, mouseY, partialTicks);
            recipeBook.renderGhostRecipe(matrix, leftPos, topPos, false, partialTicks);
        }

        renderTooltip(matrix, mouseX, mouseY);
        recipeBook.renderTooltip(matrix, leftPos, topPos, mouseX, mouseY);
        oldMouseX = (float) mouseX;
        oldMouseY = (float) mouseY;
    }

    @Override
    protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bind(TEXTURE);
        int i = leftPos;
        int j = topPos;
        blit(matrix, i, j, 0, 0, imageWidth, imageHeight);
        if (useMousePos) {
            oldMouseX = (float) mouseX;
            oldMouseY = (float) mouseY;
            useMousePos = false;
        }
        InventoryScreen.renderEntityInInventory(i + 51, j + 75, 30, (float) (i + 51) - oldMouseX, (float) (j + 75 - 50) - oldMouseY, mc.player);
    }

    @Override
    protected void renderLabels(MatrixStack matrix, int mouseX, int mouseY) {
        font.draw(matrix, craftingText, (float) titleLabelX, (float) titleLabelY, 4210752);
    }

    @Override
    public void tick() {
        recipeBook.tick();
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (recipeBook.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
        } else {
            return (!widthTooNarrow || !recipeBook.isVisible()) && super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
        }
    }

    @Override
    public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
        if (buttonClicked) {
            buttonClicked = false;
            return true;
        } else {
            return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
        }
    }

    @Override
    protected void init() {
        super.init();
        widthTooNarrow = width < 379;
        recipeBook.init(width, height, mc, widthTooNarrow, menu);
        leftPos = recipeBook.updateScreenPosition(widthTooNarrow, width, imageWidth);
        children.add(recipeBook);
        setInitialFocus(recipeBook);
        if (!ModConfigs.CosArmorDisableRecipeBook.get()) {
            addButton(new ImageButton(leftPos + 76, topPos + 27, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, button -> {
                recipeBook.initVisuals(widthTooNarrow);
                recipeBook.toggleVisibility();
                leftPos = recipeBook.updateScreenPosition(widthTooNarrow, width, imageWidth);
                ((ImageButton) button).setPosition(leftPos + 76, topPos + 27);
                buttonClicked = true;
            }));
        }
        InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUUID());
        for (int i = 0; i < 4; i++) {
            int j = 3 - i;
            addButton(new GuiCosArmorToggleButton(leftPos + 97 + 18 * i, topPos + 61, 5, 5, new StringTextComponent(""), invCosArmor.isSkinArmor(j) ? 1 : 0, button -> {
                InventoryCosArmor inv = ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUUID());
                inv.setSkinArmor(j, !inv.isSkinArmor(j));
                ((GuiCosArmorToggleButton) button).state = inv.isSkinArmor(j) ? 1 : 0;
                ModObjects.network.sendToServer(new PacketSetSkinArmor(j, inv.isSkinArmor(j)));
            }));
        }
    }

    @Override
    public void onClose() {
        recipeBook.removed();

        super.onClose();
    }

    @Override
    public RecipeBookGui getRecipeBookComponent() {
        return recipeBook;
    }

    @Override
    protected void slotClicked(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slotIn, slotId, mouseButton, type);
        recipeBook.slotClicked(slotIn);
    }

    @Override
    protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
        boolean flag = p_195361_1_ < (double) p_195361_5_ || p_195361_3_ < (double) p_195361_6_ || p_195361_1_ >= (double) (p_195361_5_ + imageWidth) || p_195361_3_ >= (double) (p_195361_6_ + imageHeight);
        return recipeBook.hasClickedOutside(p_195361_1_, p_195361_3_, leftPos, topPos, imageWidth, imageHeight, p_195361_7_) && flag;
    }

    @Override
    protected boolean isHovering(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
        return (!widthTooNarrow || !recipeBook.isVisible()) && super.isHovering(p_195359_1_, p_195359_2_, p_195359_3_, p_195359_4_, p_195359_5_, p_195359_7_);
    }

    @Override
    public void recipesUpdated() {
        recipeBook.recipesUpdated();
    }

    private void smoothTransition() {
        if (mc.screen instanceof InventoryScreen) {
            oldMouseX = ((InventoryScreen) mc.screen).xMouse;
            oldMouseY = ((InventoryScreen) mc.screen).yMouse;
        } else if (mc.screen instanceof CreativeScreen) {
            useMousePos = true;
        }
    }

}
