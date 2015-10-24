package lain.mods.cos;

import lain.mods.cos.client.GuiEvents;
import lain.mods.cos.client.KeyHandler;
import lain.mods.cos.client.PlayerRenderHandler;
import lain.mods.cos.network.NetworkManager;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.network.packet.PacketOpenNormalInventory;
import lain.mods.cos.network.packet.PacketSyncCosArmor;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "cosmeticarmorreworked", useMetadata = true)
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
        network.registerPacket(1, PacketSyncCosArmor.class);
        network.registerPacket(2, PacketOpenCosArmorInventory.class);
        network.registerPacket(3, PacketOpenNormalInventory.class);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        if (event.getSide().isClient())
        {
            MinecraftForge.EVENT_BUS.register(new PlayerRenderHandler());
            FMLCommonHandler.instance().bus().register(keyHandler = new KeyHandler());
            MinecraftForge.EVENT_BUS.register(new GuiEvents());
        }

        MinecraftForge.EVENT_BUS.register(invMan);
        FMLCommonHandler.instance().bus().register(invMan);
    }

}
