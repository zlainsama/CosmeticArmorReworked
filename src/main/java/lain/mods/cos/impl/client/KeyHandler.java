package lain.mods.cos.impl.client;

import com.mojang.blaze3d.platform.InputConstants;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.util.function.Consumer;

public enum KeyHandler {

    INSTANCE;

    public KeyMapping keyOpenCosArmorInventory = new KeyMapping("cos.key.opencosarmorinventory", InputConstants.UNKNOWN.getValue(), "key.categories.inventory");

    private void handleClientTick(TickEvent.ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.isWindowActive())
            return;
        if (keyOpenCosArmorInventory.consumeClick() && !(mc.screen instanceof GuiCosArmorInventory))
            ModObjects.network.sendToServer(new PacketOpenCosArmorInventory());
    }

    public void registerEvents() {
        MinecraftForge.EVENT_BUS.addListener(this::handleClientTick);
    }

    public void registerKeyMappings(Consumer<KeyMapping> register) {
        register.accept(keyOpenCosArmorInventory);
    }

}
