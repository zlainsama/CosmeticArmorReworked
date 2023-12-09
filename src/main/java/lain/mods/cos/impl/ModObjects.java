package lain.mods.cos.impl;

import lain.mods.cos.impl.client.InventoryManagerClient;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.network.NetworkManager;
import lain.mods.cos.init.neoforge.NeoForgeCosmeticArmorReworked;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModObjects {

    public static final Logger logger = LogManager.getLogger(NeoForgeCosmeticArmorReworked.class);
    public static final InventoryManager invMan = FMLEnvironment.dist.isClient() ? new InventoryManagerClient() : new InventoryManager();
    public static final NetworkManager network = new NetworkManager(new ResourceLocation("cosmeticarmorreworked:main"), "3");

    public static MenuType<ContainerCosArmor> getTypeContainerCosArmor() {
        return NeoForgeCosmeticArmorReworked.typeContainerCosArmor.get();
    }

}
