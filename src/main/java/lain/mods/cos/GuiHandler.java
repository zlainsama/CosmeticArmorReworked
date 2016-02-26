package lain.mods.cos;

import lain.mods.cos.client.GuiCosArmorInventory;
import lain.mods.cos.inventory.ContainerCosArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (id)
        {
            case 1:
                return new GuiCosArmorInventory(new ContainerCosArmor(player.inventory, CosmeticArmorReworked.invMan.getCosArmorInventoryClient(player.getUniqueID()), player));
        }
        return null;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (id)
        {
            case 1:
                return new ContainerCosArmor(player.inventory, CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID()), player);
        }
        return null;
    }

}
