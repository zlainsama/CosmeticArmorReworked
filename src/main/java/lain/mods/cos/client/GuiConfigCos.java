package lain.mods.cos.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import com.google.common.collect.Lists;

public class GuiConfigCos extends GuiConfig
{

    public GuiConfigCos(GuiScreen parent)
    {
        super(parent, Lists.newArrayList(new ConfigElement(GuiEvents.getLastConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements()), "cosmeticarmorreworked", false, false, GuiConfig.getAbridgedConfigPath(GuiEvents.getLastConfig().getConfigFile().toString()));
    }

}
