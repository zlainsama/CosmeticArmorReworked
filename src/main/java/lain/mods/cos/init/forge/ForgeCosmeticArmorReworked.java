package lain.mods.cos.init.forge;

import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.GuiHandler;
import lain.mods.cos.impl.client.KeyHandler;
import lain.mods.cos.impl.client.PlayerRenderHandler;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenNormalInventory;
import lain.mods.cos.impl.network.packet.PacketSetSkinArmor;
import lain.mods.cos.impl.network.packet.PacketSyncCosArmor;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("cosmeticarmorreworked")
public class ForgeCosmeticArmorReworked
{

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void setupRegistryContainerType(RegistryEvent.Register<ContainerType<?>> event)
        {
            event.getRegistry().register(IForgeContainerType.create(ModObjects.invMan::createContainerClient).setRegistryName("cosmeticarmorreworked:inventorycosarmor"));
        }
    }

    public ForgeCosmeticArmorReworked()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        ModConfigs.registerConfigs();
    }

    private void setup(FMLCommonSetupEvent event)
    {
        ModObjects.invMan.registerEvents();
        setupNetworkPackets();
    }

    private void setupClient(FMLClientSetupEvent event)
    {
        ModObjects.invMan.registerEventsClient();
        GuiHandler.INSTANCE.registerEvents();
        KeyHandler.INSTANCE.registerEvents();
        PlayerRenderHandler.INSTANCE.registerEvents();
    }

    private void setupNetworkPackets()
    {
        ModObjects.network.registerPacket(1, PacketSyncCosArmor.class, PacketSyncCosArmor::new);
        ModObjects.network.registerPacket(2, PacketSetSkinArmor.class, PacketSetSkinArmor::new);
        ModObjects.network.registerPacket(3, PacketOpenCosArmorInventory.class, PacketOpenCosArmorInventory::new);
        ModObjects.network.registerPacket(4, PacketOpenNormalInventory.class, PacketOpenNormalInventory::new);
    }

}
