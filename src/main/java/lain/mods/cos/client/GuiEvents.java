package lain.mods.cos.client;

import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.network.packet.PacketOpenNormalInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiEvents
{

    public static Configuration getLastConfig()
    {
        return lastConfig;
    }

    public static void loadConfigs(Configuration config)
    {
        lastConfig = config;

        Property prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorGuiButton_Hidden", false);
        prop.setComment("Hide CosArmorGuiButton?");
        CosArmorGuiButton_Hidden = prop.getBoolean();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorGuiButton_Left", 65);
        prop.setComment("The distance from left of the inventory gui for CosArmorGuiButton.");
        CosArmorGuiButton_Left = prop.getInt();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorGuiButton_Top", 67);
        prop.setComment("The distance from top of the inventory gui for CosArmorGuiButton.");
        CosArmorGuiButton_Top = prop.getInt();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorToggleButton_Hidden", false);
        prop.setComment("Hide CosArmorToggleButton?");
        CosArmorToggleButton_Hidden = prop.getBoolean();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorToggleButton_Left", 59);
        prop.setComment("The distance from left of the inventory gui for CosArmorToggleButton.");
        CosArmorToggleButton_Left = prop.getInt();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorToggleButton_Top", 72);
        prop.setComment("The distance from top of the inventory gui for CosArmorToggleButton.");
        CosArmorToggleButton_Top = prop.getInt();

        if (config.hasChanged())
            config.save();
    }

    public static boolean CosArmorGuiButton_Hidden = false;
    public static int CosArmorGuiButton_Left = 65;
    public static int CosArmorGuiButton_Top = 67;
    public static boolean CosArmorToggleButton_Hidden = false;
    public static int CosArmorToggleButton_Left = 59;
    public static int CosArmorToggleButton_Top = 72;

    private static Configuration lastConfig;

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
            if (!CosArmorGuiButton_Hidden)
                event.getButtonList().add(new GuiCosArmorButton(76, gui.guiLeft + CosArmorGuiButton_Left/* 65 */, gui.guiTop + CosArmorGuiButton_Top/* 67 */, 10, 10, event.getGui() instanceof GuiCosArmorInventory ? "cos.gui.buttonNormal" : "cos.gui.buttonCos"));
            GuiCosArmorToggleButton t = new GuiCosArmorToggleButton(77, gui.guiLeft + CosArmorToggleButton_Left/* 59 */, gui.guiTop + CosArmorToggleButton_Top/* 72 */, 5, 5, "");
            t.state = PlayerRenderHandler.HideCosArmor ? 1 : 0;
            if (!CosArmorToggleButton_Hidden)
                event.getButtonList().add(t);
        }
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if ("cosmeticarmorreworked".equals(event.getModID()) == true)
            loadConfigs(getLastConfig());
    }

}
