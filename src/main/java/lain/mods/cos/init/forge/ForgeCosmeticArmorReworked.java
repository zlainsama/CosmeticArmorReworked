package lain.mods.cos.init.forge;

import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.GuiHandler;
import lain.mods.cos.impl.client.KeyHandler;
import lain.mods.cos.impl.client.PlayerRenderHandler;
import lain.mods.cos.impl.network.packet.*;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod("cosmeticarmorreworked")
public class ForgeCosmeticArmorReworked {

    public ForgeCosmeticArmorReworked() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerContainers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        ModConfigs.registerConfigs();
    }

    private void registerContainers(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.CONTAINER_TYPES, helper -> helper.register("inventorycosarmor", IForgeMenuType.create(ModObjects.invMan::createContainerClient)));
    }

    private void setup(FMLCommonSetupEvent event) {
        ModObjects.invMan.registerEvents();
        setupNetworkPackets();
    }

    private void setupClient(FMLClientSetupEvent event) {
        ModObjects.invMan.registerEventsClient();
        GuiHandler.INSTANCE.registerEvents();
        KeyHandler.INSTANCE.registerEvents();
        PlayerRenderHandler.INSTANCE.registerEvents();
    }

    private void setupNetworkPackets() {
        ModObjects.network.registerPacket(1, PacketSyncCosArmor.class, PacketSyncCosArmor::new);
        ModObjects.network.registerPacket(2, PacketSetSkinArmor.class, PacketSetSkinArmor::new);
        ModObjects.network.registerPacket(3, PacketOpenCosArmorInventory.class, PacketOpenCosArmorInventory::new);
        ModObjects.network.registerPacket(4, PacketOpenNormalInventory.class, PacketOpenNormalInventory::new);
        ModObjects.network.registerPacket(5, PacketSyncHiddenFlags.class, PacketSyncHiddenFlags::new);
        ModObjects.network.registerPacket(6, PacketSetHiddenFlags.class, PacketSetHiddenFlags::new);
    }

}
