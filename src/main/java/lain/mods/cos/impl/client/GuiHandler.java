package lain.mods.cos.impl.client;

import com.google.common.collect.ImmutableSet;
import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.GuiCosArmorButton;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import lain.mods.cos.impl.client.gui.GuiCosArmorToggleButton;
import lain.mods.cos.impl.client.gui.IShiftingWidget;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenNormalInventory;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;

public enum GuiHandler {

    INSTANCE;

    public static final Set<Integer> ButtonIds = ImmutableSet.of(76, 77);

    private int lastLeft = 0;

    private void handleGuiDrawPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (event.getGui() instanceof ContainerScreen) {
            ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();

            if (lastLeft != screen.guiLeft) {
                int diffLeft = screen.guiLeft - lastLeft;
                lastLeft = screen.guiLeft;
                screen.buttons.stream().filter(IShiftingWidget.class::isInstance).map(IShiftingWidget.class::cast).forEach(b -> b.shiftLeft(diffLeft));
            }
        }
    }

    private void handleGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof ContainerScreen) {
            ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();

            lastLeft = screen.guiLeft;
        }

        if (event.getGui() instanceof InventoryScreen || event.getGui() instanceof GuiCosArmorInventory) {
            ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();

            if (!ModConfigs.CosArmorGuiButton_Hidden.get()) {
                event.addWidget(new GuiCosArmorButton(screen.guiLeft + ModConfigs.CosArmorGuiButton_Left.get()/* 65 */, screen.guiTop + ModConfigs.CosArmorGuiButton_Top.get()/* 67 */, 10, 10, event.getGui() instanceof GuiCosArmorInventory ? new TranslationTextComponent("cos.gui.buttonnormal") : new TranslationTextComponent("cos.gui.buttoncos"), button -> {
                    if (screen instanceof GuiCosArmorInventory) {
                        InventoryScreen newGui = new InventoryScreen(screen.getMinecraft().player);
                        newGui.oldMouseX = ((GuiCosArmorInventory) screen).oldMouseX;
                        newGui.oldMouseY = ((GuiCosArmorInventory) screen).oldMouseY;
                        screen.getMinecraft().displayGuiScreen(newGui);
                        ModObjects.network.sendToServer(new PacketOpenNormalInventory());
                    } else {
                        ModObjects.network.sendToServer(new PacketOpenCosArmorInventory());
                    }
                }));
            }
            if (!ModConfigs.CosArmorToggleButton_Hidden.get()) {
                event.addWidget(new GuiCosArmorToggleButton(screen.guiLeft + ModConfigs.CosArmorToggleButton_Left.get()/* 59 */, screen.guiTop + ModConfigs.CosArmorToggleButton_Top.get()/* 72 */, 5, 5, new StringTextComponent(""), PlayerRenderHandler.Disabled ? 1 : 0, button -> {
                    PlayerRenderHandler.Disabled = !PlayerRenderHandler.Disabled;
                    ((GuiCosArmorToggleButton) button).state = PlayerRenderHandler.Disabled ? 1 : 0;
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
        ScreenManager.registerFactory(ModObjects.typeContainerCosArmor, GuiCosArmorInventory::new);
    }

}
