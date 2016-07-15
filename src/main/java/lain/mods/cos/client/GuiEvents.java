package lain.mods.cos.client;

import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.network.packet.PacketOpenNormalInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiEvents
{

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void guiPostAction(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiCosArmorInventory)
        {
            if (event.getButton().id == 76)
            {
                if (event.getGui() instanceof GuiCosArmorInventory)
                {
                    event.getGui().mc.displayGuiScreen(new GuiInventory(event.getGui().mc.thePlayer));
                    CosmeticArmorReworked.network.sendToServer(new PacketOpenNormalInventory());
                }
                else
                {
                    CosmeticArmorReworked.network.sendToServer(new PacketOpenCosArmorInventory());
                }
            }
            else if (event.getButton().id == 77)
            {
                PlayerRenderHandler.HideCosArmor = !PlayerRenderHandler.HideCosArmor;
                ((GuiCosArmorToggleButton) event.getButton()).state = PlayerRenderHandler.HideCosArmor ? 1 : 0;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiCosArmorInventory)
        {
            GuiContainer gui = (GuiContainer) event.getGui();
            event.getButtonList().add(new GuiCosArmorButton(76, gui.guiLeft + 65, gui.guiTop + 67, 10, 10, event.getGui() instanceof GuiCosArmorInventory ? "cos.gui.buttonNormal" : "cos.gui.buttonCos"));
            GuiCosArmorToggleButton t = new GuiCosArmorToggleButton(77, gui.guiLeft + 59, gui.guiTop + 72, 5, 5, "");
            t.state = PlayerRenderHandler.HideCosArmor ? 1 : 0;
            event.getButtonList().add(t);
        }
    }

}
