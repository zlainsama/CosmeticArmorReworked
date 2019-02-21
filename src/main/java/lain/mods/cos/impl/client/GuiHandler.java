package lain.mods.cos.impl.client;

import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.GuiCosArmorButton;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import lain.mods.cos.impl.client.gui.GuiCosArmorToggleButton;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenNormalInventory;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

public enum GuiHandler
{

    INSTANCE;

    private void handleGuiActionPost(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiCosArmorInventory)
        {
            GuiContainer gui = (GuiContainer) event.getGui();
            for (GuiButton button : gui.buttons)
            {
                switch (button.id)
                {
                    case 76:
                        button.x = gui.guiLeft + ModConfigs.CosArmorGuiButton_Left.get();
                        break;
                    case 77:
                        button.x = gui.guiLeft + ModConfigs.CosArmorToggleButton_Left.get();
                        break;
                }
            }
        }
    }

    private void handleGuiInitPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiCosArmorInventory)
        {
            GuiContainer gui = (GuiContainer) event.getGui();
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
        MinecraftForge.EVENT_BUS.addListener(this::handleGuiActionPost);
        MinecraftForge.EVENT_BUS.addListener(this::handleGuiInitPost);
    }

}
