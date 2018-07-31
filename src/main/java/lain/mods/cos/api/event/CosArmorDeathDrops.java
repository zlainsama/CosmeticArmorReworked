package lain.mods.cos.api.event;

import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is fired whenever a player dies and the associated CAStacks is about to be dropped. <br>
 * <br>
 * 
 * {@link #player} contains the instance of EntityPlayer for the event.<br>
 * {@link #stacks} contains the instance of CAStacks for the player.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If the event is canceled, the CAStacks for the player will not be altered and nothing will be added to the drops.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 * 
 */
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
