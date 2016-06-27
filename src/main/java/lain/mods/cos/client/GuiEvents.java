package lain.mods.cos.client;

import java.lang.reflect.Method;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.network.packet.PacketOpenNormalInventory;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiEvents
{

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean isNeiHidden()
    {
        boolean hidden = true;
        try
        {
            if (isNEIHidden == null)
            {
                Class fake = Class.forName("codechicken.nei.NEIClientConfig");
                isNEIHidden = fake.getMethod("isHidden");
            }
            hidden = (Boolean) isNEIHidden.invoke(null);
        }
        catch (Exception ex)
        {
        }
        return hidden;
    }

    static Method isNEIHidden;

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
            int xSize = 176;
            int ySize = 166;

            int guiLeft = (event.getGui().width - xSize) / 2;
            int guiTop = (event.getGui().height - ySize) / 2;

            if (!event.getGui().mc.thePlayer.getActivePotionEffects().isEmpty() && isNeiHidden())
            {
                guiLeft = 160 + (event.getGui().width - xSize - 200) / 2;
            }

            event.getButtonList().add(new GuiCosArmorButton(76, guiLeft + 65, guiTop + 67, 10, 10, event.getGui() instanceof GuiCosArmorInventory ? "cos.gui.buttonNormal" : "cos.gui.buttonCos"));
            GuiCosArmorToggleButton t = new GuiCosArmorToggleButton(77, guiLeft + 59, guiTop + 72, 5, 5, "");
            t.state = PlayerRenderHandler.HideCosArmor ? 1 : 0;
            event.getButtonList().add(t);
        }
    }

}
