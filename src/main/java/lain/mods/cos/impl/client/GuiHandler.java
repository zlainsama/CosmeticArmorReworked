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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;

public enum GuiHandler {

    INSTANCE;

    public static final Set<Integer> ButtonIds = ImmutableSet.of(76, 77);

    private int lastLeft;
    private int lastCreativeTabIndex;

    private void handleGuiDrawPre(ScreenEvent.DrawScreenEvent.Pre event) {
        if (event.getScreen() instanceof AbstractContainerScreen) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();

            if (lastLeft != screen.leftPos) {
                int diffLeft = screen.leftPos - lastLeft;
                lastLeft = screen.leftPos;
                screen.children.stream().filter(IShiftingWidget.class::isInstance).map(IShiftingWidget.class::cast).forEach(b -> b.shiftLeft(diffLeft));
            }
            if (event.getScreen() instanceof CreativeModeInventoryScreen) {
                int currentTabIndex = CreativeModeInventoryScreen.selectedTab;
                if (lastCreativeTabIndex != currentTabIndex) {
                    lastCreativeTabIndex = currentTabIndex;
                    screen.children.stream().filter(ICreativeInvWidget.class::isInstance).map(ICreativeInvWidget.class::cast).forEach(b -> b.onSelectedTabChanged(currentTabIndex));
                }
            }
        }
    }

    private void handleGuiInitPost(ScreenEvent.InitScreenEvent.Post event) {
        if (event.getScreen() instanceof AbstractContainerScreen) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();

            lastLeft = screen instanceof CreativeModeInventoryScreen ? 0 : screen.leftPos;
            lastCreativeTabIndex = -1;
        }

        if (event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof GuiCosArmorInventory) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();

            if (!ModConfigs.CosArmorGuiButton_Hidden.get()) {
                event.addListener(new GuiCosArmorButton(
                        screen.leftPos + ModConfigs.CosArmorGuiButton_Left.get()/* 65 */,
                        screen.topPos + ModConfigs.CosArmorGuiButton_Top.get()/* 67 */,
                        10, 10,
                        event.getScreen() instanceof GuiCosArmorInventory ?
                                new TranslatableComponent("cos.gui.buttonnormal") :
                                new TranslatableComponent("cos.gui.buttoncos"),
                        button -> {
                            if (screen instanceof GuiCosArmorInventory) {
                                InventoryScreen newGui = new InventoryScreen(screen.getMinecraft().player);
                                newGui.xMouse = ((GuiCosArmorInventory) screen).oldMouseX;
                                newGui.yMouse = ((GuiCosArmorInventory) screen).oldMouseY;
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
                        screen.leftPos + ModConfigs.CosArmorToggleButton_Left.get()/* 59 */,
                        screen.topPos + ModConfigs.CosArmorToggleButton_Top.get()/* 72 */,
                        5, 5,
                        new TextComponent(""),
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
                        screen.topPos + ModConfigs.CosArmorCreativeGuiButton_Top.get()/* 38 */,
                        10, 10,
                        new TranslatableComponent("cos.gui.buttoncos"),
                        button -> {
                            ModObjects.network.sendToServer(new PacketOpenCosArmorInventory());
                        },
                        (button, newTabIndex) -> {
                            button.visible = newTabIndex == CreativeModeTab.TAB_INVENTORY.getId();
                        }));
            }
        }
    }

    public void registerEvents() {
        MinecraftForge.EVENT_BUS.addListener(this::handleGuiDrawPre);
        MinecraftForge.EVENT_BUS.addListener(this::handleGuiInitPost);
        setupGuiFactory();
    }

    private void setupGuiFactory() {
        MenuScreens.register(ModObjects.typeContainerCosArmor, GuiCosArmorInventory::new);
    }

}
