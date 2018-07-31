package lain.mods.cos;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ModConfigs
{

    public static Configuration getLastConfig()
    {
        return lastConfig;
    }

    public static void loadConfigs(Configuration config)
    {
        lastConfig = config;

        Property prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorGuiButton_Hidden", false);
        prop.setComment("Hide CosArmorGuiButton? (this has no effect on the server side)");
        CosArmorGuiButton_Hidden = prop.getBoolean();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorGuiButton_Left", 65);
        prop.setComment("The distance from left of the inventory gui for CosArmorGuiButton. (this has no effect on the server side)");
        CosArmorGuiButton_Left = prop.getInt();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorGuiButton_Top", 67);
        prop.setComment("The distance from top of the inventory gui for CosArmorGuiButton. (this has no effect on the server side)");
        CosArmorGuiButton_Top = prop.getInt();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorToggleButton_Hidden", false);
        prop.setComment("Hide CosArmorToggleButton? (this has no effect on the server side)");
        CosArmorToggleButton_Hidden = prop.getBoolean();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorToggleButton_Left", 59);
        prop.setComment("The distance from left of the inventory gui for CosArmorToggleButton. (this has no effect on the server side)");
        CosArmorToggleButton_Left = prop.getInt();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorToggleButton_Top", 72);
        prop.setComment("The distance from top of the inventory gui for CosArmorToggleButton. (this has no effect on the server side)");
        CosArmorToggleButton_Top = prop.getInt();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorToggleButton_Baubles", true);
        prop.setComment("Add buttons to bauble slots so that you can hide them. (this has no effect on the server side)");
        CosArmorToggleButton_Baubles = prop.getBoolean();

        prop = config.get(Configuration.CATEGORY_GENERAL, "CosArmorKeepThroughDeath", false);
        prop.setComment("If you want to keep your cosmetic slots through death, change this to true. (if you are on a server, only the setting on the server side will take effect)");
        CosArmorKeepThroughDeath = prop.getBoolean();

        if (config.hasChanged())
            config.save();
    }

    public static boolean CosArmorGuiButton_Hidden = false;
    public static int CosArmorGuiButton_Left = 65;
    public static int CosArmorGuiButton_Top = 67;
    public static boolean CosArmorToggleButton_Hidden = false;
    public static int CosArmorToggleButton_Left = 59;
    public static int CosArmorToggleButton_Top = 72;
    public static boolean CosArmorToggleButton_Baubles = true;
    public static boolean CosArmorKeepThroughDeath = false;

    private static Configuration lastConfig;

}
