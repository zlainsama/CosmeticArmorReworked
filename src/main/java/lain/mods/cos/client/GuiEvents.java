package lain.mods.cos.client;

import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.network.packet.PacketOpenNormalInventory;
import lain.mods.cos.network.packet.PacketSetSkinArmor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import baubles.common.container.SlotBauble;

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

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorToggleButton_Baubles", true);
        prop.setComment("Add buttons to bauble slots so that you can hide them.");
        CosArmorToggleButton_Baubles = prop.getBoolean();

        if (config.hasChanged())
            config.save();
    }

    public static boolean CosArmorGuiButton_Hidden = false;
    public static int CosArmorGuiButton_Left = 65;
    public static int CosArmorGuiButton_Top = 67;
    public static boolean CosArmorToggleButton_Hidden = false;
    public static int CosArmorToggleButton_Left = 59;
    public static int CosArmorToggleButton_Top = 72;
    public static boolean CosArmorToggleButton_Baubles = true;

    private static Configuration lastConfig;

    private static final boolean isBaublesLoaded = Loader.isModLoaded("baubles");

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void guiPostAction(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiCosArmorInventory)
        {
            GuiContainer gui = (GuiContainer) event.getGui();

            for (GuiButton t : event.getButtonList())
            {
                switch (t.id)
                {
                    case 76:
                        t.x = gui.guiLeft + CosArmorGuiButton_Left;
                        t.y = gui.guiTop + CosArmorGuiButton_Top;
                        break;
                    case 77:
                        t.x = gui.guiLeft + CosArmorToggleButton_Left;
                        t.y = gui.guiTop + CosArmorToggleButton_Top;
                        break;
                }
            }

            if (event.getButton().id == 76)
            {
                if (gui instanceof GuiCosArmorInventory)
                {
                    gui.mc.displayGuiScreen(new GuiInventory(gui.mc.player));
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

        if (isBaublesLoaded && CosArmorToggleButton_Baubles)
        {
            try
            {
                if (event.getGui() instanceof GuiContainer)
                {
                    GuiContainer gui = (GuiContainer) event.getGui();
                    for (Slot slot : gui.inventorySlots.inventorySlots)
                    {
                        if (slot instanceof SlotBauble)
                        {
                            int b = ReflectionHelper.getPrivateValue(SlotBauble.class, (SlotBauble) slot, "baubleSlot");
                            GuiCosArmorToggleButton t = new GuiCosArmorToggleButton(84 + b, gui.guiLeft + slot.xPos - 1, gui.guiTop + slot.yPos - 1, 5, 5, "");
                            t.state = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(gui.mc.player.getUniqueID()).isSkinArmor(4 + b) ? 1 : 0;
                            event.getButtonList().add(t);
                        }
                    }
                }
            }
            catch (Throwable ignored)
            {
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void guiPreMouse(GuiScreenEvent.MouseInputEvent.Pre event)
    {
        if (event.getGui() instanceof GuiContainer)
        {
            GuiContainer gui = (GuiContainer) event.getGui();
            int x = Mouse.getEventX() * gui.width / gui.mc.displayWidth;
            int y = gui.height - Mouse.getEventY() * gui.height / gui.mc.displayHeight - 1;
            int b = Mouse.getEventButton();
            if (Mouse.getEventButtonState())
            {
                for (int k = 0; k < gui.buttonList.size(); ++k)
                {
                    if (gui.buttonList.get(k) instanceof GuiCosArmorToggleButton)
                    {
                        GuiCosArmorToggleButton t = (GuiCosArmorToggleButton) gui.buttonList.get(k);

                        int id = t.id;
                        if (id >= 80 && id < 91)
                        {
                            if (t.mousePressed(gui.mc, x, y))
                            {
                                if (b == 0)
                                {
                                    t.playPressSound(gui.mc.getSoundHandler());

                                    int i = id - 84;
                                    InventoryCosArmor inv = CosmeticArmorReworked.invMan.getCosArmorInventoryClient(gui.mc.player.getUniqueID());
                                    inv.setSkinArmor(4 + i, !inv.isSkinArmor(4 + i));
                                    inv.markDirty();
                                    t.state = inv.isSkinArmor(4 + i) ? 1 : 0;
                                    CosmeticArmorReworked.network.sendToServer(new PacketSetSkinArmor(gui.mc.player, 4 + i));
                                }
                                event.setCanceled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if ("cosmeticarmorreworked".equals(event.getModID()) == true)
            loadConfigs(getLastConfig());
    }

}
