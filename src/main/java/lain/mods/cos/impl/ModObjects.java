package lain.mods.cos.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lain.mods.cos.impl.client.InventoryManagerClient;
import lain.mods.cos.impl.network.NetworkManager;
import lain.mods.cos.init.forge.ForgeCosmeticArmorReworked;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;

public class ModObjects
{

    public static final Logger logger = LogManager.getLogger(ForgeCosmeticArmorReworked.class);
    public static final InventoryManager invMan = DistExecutor.runForDist(() -> InventoryManagerClient::new, () -> InventoryManager::new);
    public static final NetworkManager network = new NetworkManager(new ResourceLocation("cosmeticarmorreworked:main"), "1");

}
