package lain.mods.cos.impl.client;

import com.google.common.collect.ImmutableSet;
import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.*;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenNormalInventory;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Set;

public enum GuiHandler {

    INSTANCE;

    public static final Set<Integer> ButtonIds = ImmutableSet.of(76, 77);

    private int lastLeft;
    private boolean lastInventoryOpen;

    private void handleGuiDrawPre(ScreenEvent.Render.Pre event) {
        if (event.getScreen() instanceof AbstractContainerScreen) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();

            if (lastLeft != screen.getGuiLeft()) {
                int diffLeft = screen.getGuiLeft() - lastLeft;
                lastLeft = screen.getGuiLeft();
                screen.children().stream().filter(IShiftingWidget.class::isInstance).map(IShiftingWidget.class::cast).forEach(b -> b.shiftLeft(diffLeft));
            }
            if (event.getScreen() instanceof CreativeModeInventoryScreen) {
                boolean isInventoryOpen = ((CreativeModeInventoryScreen) event.getScreen()).isInventoryOpen();
                if (lastInventoryOpen != isInventoryOpen) {
                    lastInventoryOpen = isInventoryOpen;
                    screen.children().stream().filter(ICreativeInvWidget.class::isInstance).map(ICreativeInvWidget.class::cast).forEach(b -> b.onSelectedTabChanged(isInventoryOpen));
                }
            }
        }
    }

    private void handleGuiInitPost(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof AbstractContainerScreen) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();

            lastLeft = screen instanceof CreativeModeInventoryScreen ? 0 : screen.getGuiLeft();
            lastInventoryOpen = true;
        }

        if (event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof GuiCosArmorInventory) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();

            if (!ModConfigs.CosArmorGuiButton_Hidden.get()) {
                event.addListener(new GuiCosArmorButton(
                        screen.getGuiLeft() + ModConfigs.CosArmorGuiButton_Left.get()/* 65 */,
                        screen.getGuiTop() + ModConfigs.CosArmorGuiButton_Top.get()/* 67 */,
                        10, 10,
                        event.getScreen() instanceof GuiCosArmorInventory ?
                                Component.translatable("cos.gui.buttonnormal") :
                                Component.translatable("cos.gui.buttoncos"),
                        button -> {
                            if (screen instanceof GuiCosArmorInventory) {
                                InventoryScreen newGui = new InventoryScreen(screen.getMinecraft().player);
                                InventoryScreenAccess.setXMouse(newGui, ((GuiCosArmorInventory) screen).oldMouseX);
                                InventoryScreenAccess.setYMouse(newGui, ((GuiCosArmorInventory) screen).oldMouseY);
                                screen.getMinecraft().setScreen(newGui);
                                ModObjects.network.sendToServer(new PacketOpenNormalInventory());
                            } else {
                                ModObjects.network.sendToServer(new PacketOpenCosArmorInventory());
                            }
                        },
                        null));
            }
            if (!ModConfigs.CosArmorToggleButton_Hidden.get()) {
                event.addListener(new GuiCosArmorToggleButton(
                        screen.getGuiLeft() + ModConfigs.CosArmorToggleButton_Left.get()/* 59 */,
                        screen.getGuiTop() + ModConfigs.CosArmorToggleButton_Top.get()/* 72 */,
                        5, 5,
                        Component.empty(),
                        PlayerRenderHandler.Disabled ? 1 : 0,
                        button -> {
                            PlayerRenderHandler.Disabled = !PlayerRenderHandler.Disabled;
                            ((GuiCosArmorToggleButton) button).state = PlayerRenderHandler.Disabled ? 1 : 0;
                        }));
            }
        } else if (event.getScreen() instanceof CreativeModeInventoryScreen) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();

            if (!ModConfigs.CosArmorCreativeGuiButton_Hidden.get()) {
                event.addListener(new GuiCosArmorButton(
                        /*screen.leftPos + */ModConfigs.CosArmorCreativeGuiButton_Left.get()/* 95 */,
                        screen.getGuiTop() + ModConfigs.CosArmorCreativeGuiButton_Top.get()/* 38 */,
                        10, 10,
                        Component.translatable("cos.gui.buttoncos"),
                        button -> {
                            ModObjects.network.sendToServer(new PacketOpenCosArmorInventory());
                        },
                        (button, isInventoryOpen) -> {
                            button.visible = isInventoryOpen;
                        }));
            }
        }
    }

    public void registerEvents() {
        NeoForge.EVENT_BUS.addListener(this::handleGuiDrawPre);
        NeoForge.EVENT_BUS.addListener(this::handleGuiInitPost);
        setupGuiFactory();
    }

    private void setupGuiFactory() {
        MenuScreens.register(ModObjects.getTypeContainerCosArmor(), GuiCosArmorInventory::new);
    }

}
