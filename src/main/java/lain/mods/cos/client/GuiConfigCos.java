package lain.mods.cos.client;

import com.google.common.collect.Lists;
import lain.mods.cos.ModConfigs;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiConfigCos extends GuiConfig
{

    public GuiConfigCos(GuiScreen parent)
    {
        super(parent, Lists.newArrayList(new ConfigElement(ModConfigs.getLastConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements()), "cosmeticarmorreworked", false, false, GuiConfig.getAbridgedConfigPath(ModConfigs.getLastConfig().getConfigFile().toString()));
    }

}
