package lain.mods.cos;

import lain.mods.cos.client.GuiEvents;
import lain.mods.cos.client.KeyHandler;
import lain.mods.cos.client.PlayerRenderHandler;
import lain.mods.cos.network.NetworkManager;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.network.packet.PacketOpenNormalInventory;
import lain.mods.cos.network.packet.PacketSetSkinArmor;
import lain.mods.cos.network.packet.PacketSyncCosArmor;
import lain.mods.cos.ref.RefStrings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = RefStrings.MODID, acceptedMinecraftVersions = RefStrings.MCVERSIONS)
public class CosmeticArmorReworked {

    @Mod.Instance("cosmeticarmorreworked")
    public static CosmeticArmorReworked instance;

    @SideOnly(Side.CLIENT)
    public static KeyHandler keyHandler;

    @SidedProxy(serverSide = RefStrings.SERVERSIDE, clientSide = RefStrings.CLIENTSIDE)
    public static InventoryManager invMan;

    public static final NetworkManager network = new NetworkManager(RefStrings.SHORTNAME);

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        network.registerPacket(1, PacketSyncCosArmor.class);
        network.registerPacket(2, PacketSetSkinArmor.class);
        network.registerPacket(3, PacketOpenCosArmorInventory.class);
        network.registerPacket(4, PacketOpenNormalInventory.class);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        if (event.getSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new PlayerRenderHandler());
            MinecraftForge.EVENT_BUS.register(keyHandler = new KeyHandler());
            MinecraftForge.EVENT_BUS.register(new GuiEvents());
        }

        MinecraftForge.EVENT_BUS.register(invMan);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        invMan.onServerStarting();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        invMan.onServerStopping();
    }

}
