package lain.mods.cos.api;

import java.util.UUID;
import lain.mods.cos.api.inventory.CAStacksBase;
import lain.mods.cos.impl.ModObjects;

public class CosArmorAPI
{

    /**
     * @param uuid the UniqueID of a player (Use {@link net.minecraft.entity.player.EntityPlayer#getUniqueID() EntityPlayer.getUniqueID()})
     * @return associated {@link CAStacksBase CAStacks} for the input uuid
     */
    public static CAStacksBase getCAStacks(UUID uuid)
    {
        return ModObjects.invMan.getCosArmorInventory(uuid);
    }

    /**
     * @param uuid the UniqueID of a player (Use {@link net.minecraft.entity.player.EntityPlayer#getUniqueID() EntityPlayer.getUniqueID()})
     * @return associated {@link CAStacksBase CAStacks} for the input uuid on the Client
     * @throws UnsupportedOperationException if called in a DedicatedServer
     */
    public static CAStacksBase getCAStacksClient(UUID uuid)
    {
        return ModObjects.invMan.getCosArmorInventoryClient(uuid);
    }

}
