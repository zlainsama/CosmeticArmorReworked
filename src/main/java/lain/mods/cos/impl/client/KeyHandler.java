package lain.mods.cos.impl.client;

import com.mojang.blaze3d.platform.InputConstants;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;

import java.util.function.Consumer;

public enum KeyHandler {

    INSTANCE;

    private final Minecraft mc = Minecraft.getInstance();

    public KeyMapping keyOpenCosArmorInventory = new KeyMapping("cos.key.opencosarmorinventory", InputConstants.UNKNOWN.getValue(), "key.categories.inventory");

    private void handleClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !mc.isWindowActive())
            return;
        if (keyOpenCosArmorInventory.consumeClick() && !(mc.screen instanceof GuiCosArmorInventory))
            ModObjects.network.sendToServer(new PacketOpenCosArmorInventory());
    }

    public void registerEvents() {
        NeoForge.EVENT_BUS.addListener(this::handleClientTick);
    }

    public void registerKeyMappings(Consumer<KeyMapping> register) {
        register.accept(keyOpenCosArmorInventory);
    }

}
