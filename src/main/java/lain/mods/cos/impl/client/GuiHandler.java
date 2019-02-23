package lain.mods.cos.impl.client;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import com.google.common.collect.ImmutableSet;
import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.GuiCosArmorButton;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import lain.mods.cos.impl.client.gui.GuiCosArmorToggleButton;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenNormalInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModLoadingContext;

public enum GuiHandler
{

    INSTANCE;

    public static final Set<Integer> ButtonIds = ImmutableSet.of(76, 77);

    private final Map<ResourceLocation, Function<PacketBuffer, GuiScreen>> GuiFactory = new ConcurrentHashMap<>();
    private int lastLeft = -1;

    // TODO move to use ActionPerformedEvent.Post when they are back
    private void handleGuiDrawPre(GuiScreenEvent.DrawScreenEvent.Pre event)
    {
        if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiCosArmorInventory)
        {
            GuiContainer gui = (GuiContainer) event.getGui();
            if (lastLeft != gui.guiLeft)
            {
                int diffLeft = gui.guiLeft - lastLeft;
                lastLeft = gui.guiLeft;
                gui.buttons.stream().filter(b -> ButtonIds.contains(b.id)).forEach(b -> b.x += diffLeft);
            }
        }
    }

    private void handleGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiCosArmorInventory)
        {
            GuiContainer gui = (GuiContainer) event.getGui();
            lastLeft = gui.guiLeft;
            if (!ModConfigs.CosArmorGuiButton_Hidden.get())
                event.addButton(new GuiCosArmorButton(76, gui.guiLeft + ModConfigs.CosArmorGuiButton_Left.get()/* 65 */, gui.guiTop + ModConfigs.CosArmorGuiButton_Top.get()/* 67 */, 10, 10, event.getGui() instanceof GuiCosArmorInventory ? "cos.gui.buttonnormal" : "cos.gui.buttoncos")
                {

                    @Override
                    public void onClick(double mouseX, double mouseY)
                    {
                        if (gui instanceof GuiCosArmorInventory)
                        {
                            GuiInventory newGui = new GuiInventory(gui.mc.player);
                            newGui.oldMouseX = ((GuiCosArmorInventory) gui).oldMouseX;
                            newGui.oldMouseY = ((GuiCosArmorInventory) gui).oldMouseY;
                            gui.mc.displayGuiScreen(newGui);
                            ModObjects.network.sendToServer(new PacketOpenNormalInventory());
                        }
                        else
                        {
                            ModObjects.network.sendToServer(new PacketOpenCosArmorInventory());
                        }
                    }

                });
            if (!ModConfigs.CosArmorToggleButton_Hidden.get())
                event.addButton(new GuiCosArmorToggleButton(77, gui.guiLeft + ModConfigs.CosArmorToggleButton_Left.get()/* 59 */, gui.guiTop + ModConfigs.CosArmorToggleButton_Top.get()/* 72 */, 5, 5, "", PlayerRenderHandler.Disabled ? 1 : 0)
                {

                    @Override
                    public void onClick(double mouseX, double mouseY)
                    {
                        PlayerRenderHandler.Disabled = !PlayerRenderHandler.Disabled;
                        state = PlayerRenderHandler.Disabled ? 1 : 0;
                    }

                });
        }
        // TODO add baubles integration
    }

    public void registerEvents()
    {
        MinecraftForge.EVENT_BUS.addListener(this::handleGuiDrawPre);
        MinecraftForge.EVENT_BUS.addListener(this::handleGuiInitPost);
        setupGuiFactory();
    }

    public void registerGui(ResourceLocation id, Function<PacketBuffer, GuiScreen> factory)
    {
        if (GuiFactory.containsKey(id))
            throw new IllegalArgumentException(id + " is already registered");
        GuiFactory.put(id, factory);
    }

    private void setupGuiFactory()
    {
        registerGui(InventoryCosArmor.GuiID, pb -> {
            Minecraft mc = LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
            GuiCosArmorInventory newGui = new GuiCosArmorInventory(new ContainerCosArmor(mc.player.inventory, ModObjects.invMan.getCosArmorInventoryClient(mc.player.getUniqueID()), mc.player));
            GuiScreen gui = mc.currentScreen;
            if (gui instanceof GuiInventory)
            {
                newGui.oldMouseX = ((GuiInventory) gui).oldMouseX;
                newGui.oldMouseY = ((GuiInventory) gui).oldMouseY;
            }
            return newGui;
        });
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> msg -> GuiFactory.get(msg.getId()).apply(msg.getAdditionalData()));
    }

}
