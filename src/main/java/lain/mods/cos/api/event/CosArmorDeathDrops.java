package lain.mods.cos.api.event;

import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class CosArmorDeathDrops extends Event
{

    private final EntityPlayer player;
    private final CAStacksBase stacks;

    public CosArmorDeathDrops(EntityPlayer player, CAStacksBase stacks)
    {
        this.player = player;
        this.stacks = stacks;
    }

    public CAStacksBase getCAStacks()
    {
        return stacks;
    }

    public EntityPlayer getEntityPlayer()
    {
        return player;
    }

}
