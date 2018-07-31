package lain.mods.cos.api;

import java.util.UUID;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraftforge.fml.common.Loader;

public class CosArmorAPI
{

    public static CAStacksBase getCAStacks(UUID uuid)
    {
        return isLoaded ? CosmeticArmorReworked.invMan.getCosArmorInventory(uuid).getStacks() : Dummy;
    }

    private static final boolean isLoaded = Loader.isModLoaded("cosmeticarmorreworked");
    private static final CAStacksBase Dummy = new CAStacksBase();

}
