package lain.mods.cos.client;

import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.network.packet.PacketOpenCosArmorInventory;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class KeyHandler
{

    public KeyBinding keyOpenCosArmorInventory = new KeyBinding("key.openCosArmorInventory", Keyboard.KEY_C, "key.categories.inventory");

    public KeyHandler()
    {
        ClientRegistry.registerKeyBinding(keyOpenCosArmorInventory);
    }

    @SubscribeEvent
    public void handleEvent(ClientTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            if (keyOpenCosArmorInventory.getIsKeyPressed() && FMLClientHandler.instance().getClient().inGameHasFocus)
                CosmeticArmorReworked.network.sendToServer(new PacketOpenCosArmorInventory());
        }
    }

}
