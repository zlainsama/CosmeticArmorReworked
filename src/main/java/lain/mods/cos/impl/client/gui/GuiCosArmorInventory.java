package lain.mods.cos.impl.client.gui;

import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.packet.PacketSetSkinArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public class GuiCosArmorInventory extends EffectRenderingInventoryScreen<ContainerCosArmor> implements RecipeUpdateListener {

    public static final ResourceLocation TEXTURE = new ResourceLocation("cosmeticarmorreworked", "textures/gui/cosarmorinventory.png");

    private final RecipeBookComponent recipeBook = new RecipeBookComponent() {

        @Override
        public boolean isVisible() {
            return super.isVisible() && !ModConfigs.CosArmorDisableRecipeBook.get();
        }

        @Override
        public void toggleVisibility() {
            setVisible(!super.isVisible());
        }

    };
    private final Component craftingText;
    private final Minecraft mc = Minecraft.getInstance();
    public float oldMouseX;
    public float oldMouseY;
    private boolean useMousePos;
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public GuiCosArmorInventory(ContainerCosArmor container, Inventory invPlayer, Component displayName) {
        super(container, invPlayer, displayName);
        titleLabelX = 97;

        craftingText = Component.translatable("container.crafting");

        smoothTransition();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (recipeBook.isVisible() && widthTooNarrow) {
            renderBackground(graphics, mouseX, mouseY, partialTicks);
            recipeBook.render(graphics, mouseX, mouseY, partialTicks);
        } else {
            super.render(graphics, mouseX, mouseY, partialTicks);
            recipeBook.render(graphics, mouseX, mouseY, partialTicks);
            recipeBook.renderGhostRecipe(graphics, leftPos, topPos, false, partialTicks);
        }

        renderTooltip(graphics, mouseX, mouseY);
        recipeBook.renderTooltip(graphics, leftPos, topPos, mouseX, mouseY);
        oldMouseX = (float) mouseX;
        oldMouseY = (float) mouseY;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int i = leftPos;
        int j = topPos;
        graphics.blit(TEXTURE, i, j, 0, 0, imageWidth, imageHeight);
        if (useMousePos) {
            oldMouseX = (float) mouseX;
            oldMouseY = (float) mouseY;
            useMousePos = false;
        }
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F, oldMouseX, oldMouseY, mc.player);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, craftingText, titleLabelX, titleLabelY, 4210752, false);
    }

    @Override
    protected void containerTick() {
        recipeBook.tick();
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if (recipeBook.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            setFocused(recipeBook);
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
        leftPos = recipeBook.updateScreenPosition(width, imageWidth);
        addWidget(recipeBook);
        setInitialFocus(recipeBook);
        if (!ModConfigs.CosArmorDisableRecipeBook.get()) {
            addRenderableWidget(new ImageButton(leftPos + 76, topPos + 27, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, button -> {
                recipeBook.toggleVisibility();
                leftPos = recipeBook.updateScreenPosition(width, imageWidth);
                ((ImageButton) button).setPosition(leftPos + 76, topPos + 27);
                buttonClicked = true;
            }));
        }
        InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUUID());
        for (int i = 0; i < 4; i++) {
            int j = 3 - i;
            addRenderableWidget(new GuiCosArmorToggleButton(leftPos + 97 + 18 * i, topPos + 61, 5, 5, Component.empty(), invCosArmor.isSkinArmor(j) ? 1 : 0, button -> {
                InventoryCosArmor inv = ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUUID());
                inv.setSkinArmor(j, !inv.isSkinArmor(j));
                ((GuiCosArmorToggleButton) button).state = inv.isSkinArmor(j) ? 1 : 0;
                ModObjects.network.sendToServer(new PacketSetSkinArmor(j, inv.isSkinArmor(j)));
            }));
        }
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {
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
            oldMouseX = InventoryScreenAccess.getXMouse((InventoryScreen) mc.screen);
            oldMouseY = InventoryScreenAccess.getYMouse((InventoryScreen) mc.screen);
        } else if (mc.screen instanceof CreativeModeInventoryScreen) {
            useMousePos = true;
        }
    }

}
