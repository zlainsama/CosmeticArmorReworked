package lain.mods.cos.impl.client;

import com.google.common.collect.ImmutableSet;
import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.*;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenNormalInventory;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;

public enum GuiHandler {

    INSTANCE;

    public static final Set<Integer> ButtonIds = ImmutableSet.of(76, 77);

    private int lastLeft;
    private int lastCreativeTabIndex;

    private void handleGuiDrawPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (event.getGui() instanceof ContainerScreen) {
            ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();

            if (lastLeft != screen.leftPos) {
                int diffLeft = screen.leftPos - lastLeft;
                lastLeft = screen.leftPos;
                screen.buttons.stream().filter(IShiftingWidget.class::isInstance).map(IShiftingWidget.class::cast).forEach(b -> b.shiftLeft(diffLeft));
            }
            if (event.getGui() instanceof CreativeScreen) {
                int currentTabIndex = CreativeScreen.selectedTab;
                if (lastCreativeTabIndex != currentTabIndex) {
                    lastCreativeTabIndex = currentTabIndex;
                    screen.buttons.stream().filter(ICreativeInvWidget.class::isInstance).map(ICreativeInvWidget.class::cast).forEach(b -> b.onSelectedTabChanged(currentTabIndex));
                }
            }
        }
    }

    private void handleGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof ContainerScreen) {
            ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();

            lastLeft = screen instanceof CreativeScreen ? 0 : screen.leftPos;
            lastCreativeTabIndex = -1;
        }

        if (event.getGui() instanceof InventoryScreen || event.getGui() instanceof GuiCosArmorInventory) {
            ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();

            if (!ModConfigs.CosArmorGuiButton_Hidden.get()) {
                event.addWidget(new GuiCosArmorButton(
                        screen.leftPos + ModConfigs.CosArmorGuiButton_Left.get()/* 65 */,
                        screen.topPos + ModConfigs.CosArmorGuiButton_Top.get()/* 67 */,
                        10, 10,
                        event.getGui() instanceof GuiCosArmorInventory ?
                                new TranslationTextComponent("cos.gui.buttonnormal") :
                                new TranslationTextComponent("cos.gui.buttoncos"),
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
                event.addWidget(new GuiCosArmorToggleButton(
                        screen.leftPos + ModConfigs.CosArmorToggleButton_Left.get()/* 59 */,
                        screen.topPos + ModConfigs.CosArmorToggleButton_Top.get()/* 72 */,
                        5, 5,
                        new StringTextComponent(""),
                        PlayerRenderHandler.Disabled ? 1 : 0,
                        button -> {
                            PlayerRenderHandler.Disabled = !PlayerRenderHandler.Disabled;
                            ((GuiCosArmorToggleButton) button).state = PlayerRenderHandler.Disabled ? 1 : 0;
                        }));
            }
        } else if (event.getGui() instanceof CreativeScreen) {
            ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();

            if (!ModConfigs.CosArmorCreativeGuiButton_Hidden.get()) {
                event.addWidget(new GuiCosArmorButton(
                        /*screen.leftPos + */ModConfigs.CosArmorCreativeGuiButton_Left.get()/* 95 */,
                        screen.topPos + ModConfigs.CosArmorCreativeGuiButton_Top.get()/* 38 */,
                        10, 10,
                        new TranslationTextComponent("cos.gui.buttoncos"),
                        button -> {
                            ModObjects.network.sendToServer(new PacketOpenCosArmorInventory());
                        },
                        (button, newTabIndex) -> {
                            button.visible = newTabIndex == ItemGroup.TAB_INVENTORY.getId();
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
        ScreenManager.register(ModObjects.typeContainerCosArmor, GuiCosArmorInventory::new);
    }

}
