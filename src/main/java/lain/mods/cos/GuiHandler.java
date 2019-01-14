package lain.mods.cos;

import lain.mods.cos.client.GuiCosArmorInventory;
import lain.mods.cos.inventory.ContainerCosArmor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (id)
        {
            case 1:
                GuiCosArmorInventory newGui = new GuiCosArmorInventory(new ContainerCosArmor(player.inventory, CosmeticArmorReworked.invMan.getCosArmorInventoryClient(player.getUniqueID()), player));
                GuiScreen gui = FMLClientHandler.instance().getClient().currentScreen;
                if (gui instanceof GuiInventory)
                {
                    newGui.oldMouseX = ((GuiInventory) gui).oldMouseX;
                    newGui.oldMouseY = ((GuiInventory) gui).oldMouseY;
                }
                return newGui;
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
