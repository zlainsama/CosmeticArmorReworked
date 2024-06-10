package lain.mods.cos.impl.client.gui;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;

import java.lang.reflect.Field;

public class InventoryScreenAccess {

    private static final Field fXMouse = findField(InventoryScreen.class, "xMouse", "f_98831_");
    private static final Field fYMouse = findField(InventoryScreen.class, "yMouse", "f_98832_");

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

    private static Field findField(Class<?> clazz, String... names) {
        Throwable t = null;
        for (String name : names) {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                if (t == null)
                    t = e;
                else
                    t.addSuppressed(e);
            }
        }
        if (t != null)
            throw new RuntimeException(t);
        throw new IllegalArgumentException();
    }

}
