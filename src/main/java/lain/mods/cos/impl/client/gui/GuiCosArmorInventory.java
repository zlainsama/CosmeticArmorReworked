package lain.mods.cos.impl.client.gui;

import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.packet.PacketSetSkinArmor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiCosArmorInventory extends AbstractRecipeBookScreen<ContainerCosArmor> implements RecipeUpdateListener {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("cosmeticarmorreworked", "textures/gui/cosarmorinventory.png");
    private final EffectsInInventory effects;
    public float oldMouseX;
    public float oldMouseY;
    private boolean useMousePos;
    private boolean buttonClicked;

    public GuiCosArmorInventory(ContainerCosArmor container, Inventory invPlayer, Component displayName) {
        super(container, new CraftingRecipeBookComponent(container) {
            @Override
            public boolean isVisible() {
                return !ModConfigs.CosArmorDisableRecipeBook.get() && super.isVisible();
            }
        }, invPlayer, Component.translatable("container.crafting"));
        titleLabelX = 97;
        effects = new EffectsInInventory(this);
        smoothTransition();
    }

    @Override
    protected void init() {
        super.init();
        InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(minecraft.player.getUUID());
        for (int i = 0; i < 4; i++) {
            int j = 3 - i;
            addRenderableWidget(new GuiCosArmorToggleButton(leftPos + 97 + 18 * i, topPos + 61, 5, 5, Component.empty(), invCosArmor.isSkinArmor(j) ? 1 : 0, button -> {
                InventoryCosArmor inv = ModObjects.invMan.getCosArmorInventoryClient(minecraft.player.getUUID());
                inv.setSkinArmor(j, !inv.isSkinArmor(j));
                ((GuiCosArmorToggleButton) button).state = inv.isSkinArmor(j) ? 1 : 0;
                ModObjects.network.sendToServer(new PacketSetSkinArmor(j, inv.isSkinArmor(j)));
            }));
        }
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(leftPos + 76, topPos + 27);
    }

    @Override
    protected void onRecipeBookButtonClick() {
        buttonClicked = true;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 4210752, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        effects.render(graphics, mouseX, mouseY, partialTicks);
        oldMouseX = (float) mouseX;
        oldMouseY = (float) mouseY;
    }

    @Override
    public boolean showsActiveEffects() {
        return effects.canSeeEffects();
    }

    @Override
    protected boolean isBiggerResultSlot() {
        return false;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int i = leftPos;
        int j = topPos;
        graphics.blit(RenderType::guiTextured, TEXTURE, i, j, 0, 0, imageWidth, imageHeight, 256, 256);
        if (useMousePos) {
            oldMouseX = (float) mouseX;
            oldMouseY = (float) mouseY;
            useMousePos = false;
        }
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, i + 26, j + 8, i + 75, j + 78, 30, 0.0625F, oldMouseX, oldMouseY, minecraft.player);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int buttonId) {
        if (buttonClicked) {
            buttonClicked = false;
            return true;
        } else {
            return super.mouseReleased(mouseX, mouseY, buttonId);
        }
    }

    private void smoothTransition() {
        Minecraft mc = Minecraft.getInstance(); // "minecraft" is null before init(), so get one for now.
        if (mc.screen instanceof InventoryScreen) {
            oldMouseX = InventoryScreenAccess.getXMouse((InventoryScreen) mc.screen);
            oldMouseY = InventoryScreenAccess.getYMouse((InventoryScreen) mc.screen);
        } else if (mc.screen instanceof CreativeModeInventoryScreen) {
            useMousePos = true;
        }
    }

}
