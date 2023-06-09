package lain.mods.cos.impl.client.gui;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class InventoryScreenAccess {

    private static final Field fXMouse = ObfuscationReflectionHelper.findField(InventoryScreen.class, "f_98831_");
    private static final Field fYMouse = ObfuscationReflectionHelper.findField(InventoryScreen.class, "f_98832_");

    public static float getXMouse(InventoryScreen screen) {
        try {
            return fXMouse.getFloat(screen);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static float getYMouse(InventoryScreen screen) {
        try {
            return fYMouse.getFloat(screen);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setXMouse(InventoryScreen screen, float xMouse) {
        try {
            fXMouse.setFloat(screen, xMouse);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setYMouse(InventoryScreen screen, float yMouse) {
        try {
            fYMouse.setFloat(screen, yMouse);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
