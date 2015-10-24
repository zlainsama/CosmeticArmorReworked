package lain.mods.cos;

import lain.mods.cos.client.GuiEvents;
import lain.mods.cos.client.GuiHandlerClient;
import lain.mods.cos.client.KeyHandler;
import lain.mods.cos.client.PlayerRenderHandler;
import lain.mods.cos.network.NetworkManager;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.network.packet.PacketOpenNormalInventory;
import lain.mods.cos.network.packet.PacketSyncCosArmor;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = "cosmeticarmorreworked", useMetadata = true)
public class CosmeticArmorReworked
{

    @Mod.Instance("cosmeticarmorreworked")
    public static CosmeticArmorReworked instance;

    public static final NetworkManager network = new NetworkManager("lain|nm|cos");

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        network.registerPacket(1, PacketSyncCosArmor.class);
        network.registerPacket(2, PacketOpenCosArmorInventory.class);
        network.registerPacket(3, PacketOpenNormalInventory.class);

        if (event.getSide().isClient())
        {
            MinecraftForge.EVENT_BUS.register(new PlayerRenderHandler());
            NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandlerClient());
            FMLCommonHandler.instance().bus().register(new KeyHandler());
            MinecraftForge.EVENT_BUS.register(new GuiEvents());
        }
        else
        {
            NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
        }

    }

}
