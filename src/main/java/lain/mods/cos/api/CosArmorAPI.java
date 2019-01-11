package lain.mods.cos.api;

import java.util.UUID;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CosArmorAPI
{

    /**
     * @param uuid the UniqueID of a player (See also {@link net.minecraft.entity.player.EntityPlayer#getUniqueID() EntityPlayer.getUniqueID()})
     * @return associated {@link CAStacksBase CAStacks} for the input uuid
     */
    public static CAStacksBase getCAStacks(UUID uuid)
    {
        return CosmeticArmorReworked.invMan.getCosArmorInventory(uuid).getStacks();
    }

    /**
     * @param uuid the UniqueID of a player (See also {@link net.minecraft.entity.player.EntityPlayer#getUniqueID() EntityPlayer.getUniqueID()})
     * @return associated {@link CAStacksBase CAStacks} for the input uuid on the Client
     */
    @SideOnly(Side.CLIENT)
    public static CAStacksBase getCAStacksClient(UUID uuid)
    {
        return CosmeticArmorReworked.invMan.getCosArmorInventoryClient(uuid).getStacks();
    }

}
