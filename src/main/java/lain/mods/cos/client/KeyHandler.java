package lain.mods.cos.client;

import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class KeyHandler
{

    public KeyBinding keyOpenCosArmorInventory = new KeyBinding("cos.key.opencosarmorinventory", 0, "key.categories.inventory");

    public KeyHandler()
    {
        ClientRegistry.registerKeyBinding(keyOpenCosArmorInventory);
    }

    @SubscribeEvent
    public void handleEvent(ClientTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            if (keyOpenCosArmorInventory.isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus)
                CosmeticArmorReworked.network.sendToServer(new PacketOpenCosArmorInventory());
        }
    }

}
