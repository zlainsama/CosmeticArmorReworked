package lain.mods.cos.impl.client;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.google.common.collect.ImmutableSet;
import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.GuiCosArmorButton;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import lain.mods.cos.impl.client.gui.GuiCosArmorToggleButton;
import lain.mods.cos.impl.client.gui.IShiftingWidget;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenNormalInventory;
import lain.mods.cos.impl.network.packet.PacketSetHiddenFlags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.inventory.SlotCurio;
import top.theillusivec4.curios.common.inventory.CuriosContainer;

public enum GuiHandler
{

    INSTANCE;

    private static final boolean isCuriosLoaded = ModList.get().isLoaded("curios");

    public static final Set<Integer> ButtonIds = ImmutableSet.of(76, 77);

    private Consumer<Object> currentWidgetAdder = null;
    private Consumer<Object> currentWidgetRemover = null;
    private Optional<Field> fieldaccess_curioscontainer_lastscrollindex;
    private Optional<Field> fieldaccess_slotcurio_identifier;
    private int lastLeft = 0;
    private int lastCuriosScrollIndex = -1;

    private void handleGuiDrawPre(GuiScreenEvent.DrawScreenEvent.Pre event)
    {
        if (event.getGui() instanceof ContainerScreen)
        {
            ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();

            if (lastLeft != screen.guiLeft)
            {
                int diffLeft = screen.guiLeft - lastLeft;
                lastLeft = screen.guiLeft;
                screen.buttons.stream().filter(IShiftingWidget.class::isInstance).forEach(b -> b.x += diffLeft);
            }

            if (isCuriosLoaded)
            {
                if (fieldaccess_curioscontainer_lastscrollindex == null)
                {
                    try
                    {
                        Field f = CuriosContainer.class.getDeclaredField("lastScrollIndex");
                        f.setAccessible(true);
                        fieldaccess_curioscontainer_lastscrollindex = Optional.of(f);
                    }
                    catch (Throwable e)
                    {
                        ModObjects.logger.error("Failed in acquiring fieldaccess_curioscontainer_lastscrollindex", e);
                        fieldaccess_curioscontainer_lastscrollindex = Optional.empty();
                    }
                }
                if (fieldaccess_slotcurio_identifier == null)
                {
                    try
                    {
                        Field f = SlotCurio.class.getDeclaredField("identifier");
                        f.setAccessible(true);
                        fieldaccess_slotcurio_identifier = Optional.of(f);
                    }
                    catch (Throwable e)
                    {
                        ModObjects.logger.error("Failed in acquiring fieldaccess_slotcurio_identifier", e);
                        fieldaccess_slotcurio_identifier = Optional.empty();
                    }
                }

                if (fieldaccess_curioscontainer_lastscrollindex.isPresent() && fieldaccess_slotcurio_identifier.isPresent())
                {
                    Container container = screen.getContainer();
                    if (container instanceof CuriosContainer)
                    {
                        try
                        {
                            int index = (int) fieldaccess_curioscontainer_lastscrollindex.get().get(container);
                            if (lastCuriosScrollIndex == -1 || lastCuriosScrollIndex != index)
                            {
                                lastCuriosScrollIndex = index;

                                screen.buttons.stream()
                                        .filter(GuiCosArmorToggleButton.class::isInstance)
                                        .map(GuiCosArmorToggleButton.class::cast)
                                        .filter(button -> button.stamp == 1) // stamp 1 - curios
                                        .collect(Collectors.toList())
                                        .forEach(currentWidgetRemover);

                                Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
                                InventoryCosArmor invCosArmor = ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUniqueID());

                                for (Slot slot : container.inventorySlots)
                                {
                                    if (slot instanceof SlotCurio)
                                    {
                                        String identifier = (String) fieldaccess_slotcurio_identifier.get().get(slot) + "#" + slot.getSlotIndex();
                                        currentWidgetAdder.accept(new GuiCosArmorToggleButton(screen.guiLeft + slot.xPos - 1, screen.guiTop + slot.yPos - 1, 5, 5, "", invCosArmor.isHidden("curios", identifier) ? 1 : 0, button -> {
                                            invCosArmor.setHidden("curios", identifier, !invCosArmor.isHidden("curios", identifier));
                                            ((GuiCosArmorToggleButton) button).state = invCosArmor.isHidden("curios", identifier) ? 1 : 0;
                                            ModObjects.network.sendToServer(new PacketSetHiddenFlags("curios", identifier, invCosArmor.isHidden("curios", identifier)));
                                        }).setStamp(1)); // stamp 1 - curios
                                    }
                                }
                            }
                        }
                        catch (Throwable e)
                        {
                            ModObjects.logger.fatal("Failed in adding toggleable buttons to CurioSlots", e);
                            fieldaccess_curioscontainer_lastscrollindex = Optional.empty();
                            fieldaccess_slotcurio_identifier = Optional.empty();
                        }
                    }
                }
            }
        }
    }

    private void handleGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        currentWidgetAdder = obj -> event.addWidget((Widget) obj);
        currentWidgetRemover = obj -> event.removeWidget((Widget) obj);

        if (event.getGui() instanceof ContainerScreen)
        {
            ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();

            lastLeft = screen.guiLeft;

            if (isCuriosLoaded)
            {
                lastCuriosScrollIndex = -1;
            }
        }

        if (event.getGui() instanceof InventoryScreen || event.getGui() instanceof GuiCosArmorInventory)
        {
            ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();

            if (!ModConfigs.CosArmorGuiButton_Hidden.get())
            {
                event.addWidget(new GuiCosArmorButton(screen.guiLeft + ModConfigs.CosArmorGuiButton_Left.get()/* 65 */, screen.guiTop + ModConfigs.CosArmorGuiButton_Top.get()/* 67 */, 10, 10, event.getGui() instanceof GuiCosArmorInventory ? "cos.gui.buttonnormal" : "cos.gui.buttoncos", button -> {
                    if (screen instanceof GuiCosArmorInventory)
                    {
                        InventoryScreen newGui = new InventoryScreen(screen.getMinecraft().player);
                        newGui.oldMouseX = ((GuiCosArmorInventory) screen).oldMouseX;
                        newGui.oldMouseY = ((GuiCosArmorInventory) screen).oldMouseY;
                        screen.getMinecraft().displayGuiScreen(newGui);
                        ModObjects.network.sendToServer(new PacketOpenNormalInventory());
                    }
                    else
                    {
                        ModObjects.network.sendToServer(new PacketOpenCosArmorInventory());
                    }
                }));
            }
            if (!ModConfigs.CosArmorToggleButton_Hidden.get())
            {
                event.addWidget(new GuiCosArmorToggleButton(screen.guiLeft + ModConfigs.CosArmorToggleButton_Left.get()/* 59 */, screen.guiTop + ModConfigs.CosArmorToggleButton_Top.get()/* 72 */, 5, 5, "", PlayerRenderHandler.Disabled ? 1 : 0, button -> {
                    PlayerRenderHandler.Disabled = !PlayerRenderHandler.Disabled;
                    ((GuiCosArmorToggleButton) button).state = PlayerRenderHandler.Disabled ? 1 : 0;
                }));
            }
        }
    }

    public void registerEvents()
    {
        MinecraftForge.EVENT_BUS.addListener(this::handleGuiDrawPre);
        MinecraftForge.EVENT_BUS.addListener(this::handleGuiInitPost);
        setupGuiFactory();
    }

    private void setupGuiFactory()
    {
        ScreenManager.registerFactory(ModObjects.typeContainerCosArmor, GuiCosArmorInventory::new);
    }

}
