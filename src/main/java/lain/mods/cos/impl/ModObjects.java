package lain.mods.cos.impl;

import lain.mods.cos.impl.client.InventoryManagerClient;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.network.NetworkManager;
import lain.mods.cos.init.forge.ForgeCosmeticArmorReworked;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModObjects {

    public static final Logger logger = LogManager.getLogger(ForgeCosmeticArmorReworked.class);
    public static final InventoryManager invMan = DistExecutor.unsafeRunForDist(() -> InventoryManagerClient::new, () -> InventoryManager::new);
    public static final NetworkManager network = new NetworkManager(new ResourceLocation("cosmeticarmorreworked:main"), "2");

    public static MenuType<ContainerCosArmor> getTypeContainerCosArmor() {
        return ForgeCosmeticArmorReworked.typeContainerCosArmor.get();
    }

}
