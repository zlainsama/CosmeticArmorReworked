package lain.mods.cos;

import lain.mods.cos.client.GuiEvents;
import lain.mods.cos.client.KeyHandler;
import lain.mods.cos.client.PlayerRenderHandler;
import lain.mods.cos.network.NetworkManager;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.network.packet.PacketOpenNormalInventory;
import lain.mods.cos.network.packet.PacketSetSkinArmor;
import lain.mods.cos.network.packet.PacketSyncCosArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "cosmeticarmorreworked", useMetadata = true, acceptedMinecraftVersions = "[1.8],[1.8.8],[1.8.9]")
public class CosmeticArmorReworked
{

    @Mod.Instance("cosmeticarmorreworked")
    public static CosmeticArmorReworked instance;

    @SideOnly(Side.CLIENT)
    public static KeyHandler keyHandler;

    @SidedProxy(serverSide = "lain.mods.cos.InventoryManager", clientSide = "lain.mods.cos.client.InventoryManagerClient")
    public static InventoryManager invMan;

    public static final NetworkManager network = new NetworkManager("lain|nm|cos");

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        PlayerUtils.registerListener();

        network.registerPacket(1, PacketSyncCosArmor.class);
        network.registerPacket(2, PacketSetSkinArmor.class);
        network.registerPacket(3, PacketOpenCosArmorInventory.class);
        network.registerPacket(4, PacketOpenNormalInventory.class);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        if (event.getSide().isClient())
        {
            MinecraftForge.EVENT_BUS.register(new PlayerRenderHandler());
            MinecraftForge.EVENT_BUS.register(keyHandler = new KeyHandler());
            // FMLCommonHandler.instance().bus().register(keyHandler = new KeyHandler());
            MinecraftForge.EVENT_BUS.register(new GuiEvents());
        }

        MinecraftForge.EVENT_BUS.register(invMan);
        // FMLCommonHandler.instance().bus().register(invMan);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        invMan.onServerStarting();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        invMan.onServerStopping();
    }

}
