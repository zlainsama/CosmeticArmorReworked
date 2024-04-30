package lain.mods.cos.init.neoforge;

import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.GuiHandler;
import lain.mods.cos.impl.client.KeyHandler;
import lain.mods.cos.impl.client.PlayerRenderHandler;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.network.ModPayloads;
import lain.mods.cos.init.ModConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ModConstants.MODID)
public class NeoForgeCosmeticArmorReworked {

    private static final DeferredRegister<MenuType<?>> MENU = DeferredRegister.create(BuiltInRegistries.MENU, ModConstants.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerCosArmor>> typeContainerCosArmor = MENU.register("inventorycosarmor", () -> new MenuType<>(ModObjects.invMan::createContainerClient, FeatureFlags.VANILLA_SET));

    public NeoForgeCosmeticArmorReworked(IEventBus bus) {
        MENU.register(bus);
        bus.addListener(this::setup);
        bus.addListener(this::setupClient);
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(this::setupKeyMappings);
            bus.addListener(this::setupMenuScreens);
        }
        bus.addListener(this::setupPayloadHandlers);
        ModConfigs.registerConfigs();
    }

    private void setup(FMLCommonSetupEvent event) {
        ModObjects.invMan.registerEvents();
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

    private void setupMenuScreens(RegisterMenuScreensEvent event) {
        GuiHandler.INSTANCE.registerMenuScreens(event);
    }

    private void setupPayloadHandlers(RegisterPayloadHandlersEvent event) {
        ModPayloads.setupPayloads(event.registrar("5"));
    }

}
