package lain.mods.cos.impl;

import lain.mods.cos.impl.client.InventoryManagerClient;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.network.NetworkManager;
import lain.mods.cos.init.forge.ForgeCosmeticArmorReworked;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModObjects {

    public static final Logger logger = LogManager.getLogger(ForgeCosmeticArmorReworked.class);
    public static final InventoryManager invMan = DistExecutor.safeRunForDist(() -> InventoryManagerClient::new, () -> InventoryManager::new);
    public static final NetworkManager network = new NetworkManager(new ResourceLocation("cosmeticarmorreworked:main"), "2");

    @ObjectHolder("cosmeticarmorreworked:inventorycosarmor")
    public static ContainerType<ContainerCosArmor> typeContainerCosArmor;

}
