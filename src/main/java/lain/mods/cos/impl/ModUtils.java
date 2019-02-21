package lain.mods.cos.impl;

import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

public class ModUtils
{

    public static boolean isModLoaded(String modid)
    {
        if (modid == null)
            return false;
        return FMLLoader.getLoadingModList().getMods().stream().map(ModInfo::getModId).filter(modid::equals).findAny().isPresent();
    }

}
