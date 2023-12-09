package lain.mods.cos.api.event;

import lain.mods.cos.api.inventory.CAStacksBase;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * This event is fired whenever a player dies and the associated CAStacks is about to be dropped. <br>
 * <br>
 * <p>
 * {@link #player} contains the instance of EntityPlayer for the event.<br>
 * {@link #stacks} contains the instance of CAStacks for the player.<br>
 * <br>
 * This event is {@link ICancellableEvent Cancellable}. <br>
 * If the event is canceled, the CAStacks for the player will not be altered and nothing will be added to the drops.<br>
 * <br>
 * This event is fired on the {@link NeoForge#EVENT_BUS}.<br>
 */
public class CosArmorDeathDrops extends Event implements ICancellableEvent {

    private final Player player;
    private final CAStacksBase stacks;

    public CosArmorDeathDrops(Player player, CAStacksBase stacks) {
        this.player = player;
        this.stacks = stacks;
    }

    public CAStacksBase getCAStacks() {
        return stacks;
    }

    public Player getEntityPlayer() {
        return player;
    }

}
