package lain.mods.cos.init.neoforge;

import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.GuiHandler;
import lain.mods.cos.impl.client.KeyHandler;
import lain.mods.cos.impl.client.PlayerRenderHandler;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.network.packet.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod("cosmeticarmorreworked")
public class NeoForgeCosmeticArmorReworked {

    private static final DeferredRegister<MenuType<?>> MENU = DeferredRegister.create(BuiltInRegistries.MENU, "cosmeticarmorreworked");

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerCosArmor>> typeContainerCosArmor = MENU.register("inventorycosarmor", () -> new MenuType<>(ModObjects.invMan::createContainerClient, FeatureFlags.VANILLA_SET));

    public NeoForgeCosmeticArmorReworked() {
        MENU.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        if (FMLEnvironment.dist.isClient()) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupKeyMappings);
        }
        ModConfigs.registerConfigs();
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

    private void setupKeyMappings(RegisterKeyMappingsEvent event) {
        KeyHandler.INSTANCE.registerKeyMappings(event::register);
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
