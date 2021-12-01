package lain.mods.cos.impl.client;

import com.mojang.blaze3d.platform.InputConstants;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import lain.mods.cos.impl.network.packet.PacketOpenCosArmorInventory;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

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
        ClientRegistry.registerKeyBinding(keyOpenCosArmorInventory);
        MinecraftForge.EVENT_BUS.addListener(this::handleClientTick);
    }

}
