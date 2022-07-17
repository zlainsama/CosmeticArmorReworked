package lain.mods.cos.init.forge;

import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.GuiHandler;
import lain.mods.cos.impl.client.KeyHandler;
import lain.mods.cos.impl.client.PlayerRenderHandler;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.network.packet.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("cosmeticarmorreworked")
public class ForgeCosmeticArmorReworked {

    private static final DeferredRegister<MenuType<?>> MENU = DeferredRegister.create(ForgeRegistries.Keys.MENU_TYPES, "cosmeticarmorreworked");

    public static final RegistryObject<MenuType<ContainerCosArmor>> typeContainerCosArmor = MENU.register("inventorycosarmor", () -> IForgeMenuType.create(ModObjects.invMan::createContainerClient));

    public ForgeCosmeticArmorReworked() {
        MENU.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupKeyMappings);
        });
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
