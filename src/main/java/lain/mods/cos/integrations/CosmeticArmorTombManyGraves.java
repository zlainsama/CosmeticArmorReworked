package lain.mods.cos.integrations;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.ModConfigs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.inventory.TransitionInventory;

@Mod(modid = "cosmeticarmorreworked|tombmanygraves", useMetadata = true, acceptedMinecraftVersions = "[1.12.1, 1.13)", certificateFingerprint = "aaaf83332a11df02406e9f266b1b65c1306f0f76")
public class CosmeticArmorTombManyGraves
{

    // Code from M4thG33k/TombManyPlugins
    public class CosmeticArmorSpecialInventory extends AbstractSpecialInventory
    {

        @Nonnull
        @Override
        public List<ItemStack> getDrops(NBTBase compound)
        {
            if (compound instanceof NBTTagList)
            {
                return (new TransitionInventory((NBTTagList) compound)).getListOfNonEmptyItemStacks();
            }
            else
            {
                return new ArrayList<ItemStack>();
            }
        }

        @Override
        public int getInventoryDisplayNameColorForGui()
        {
            return 0;
        }

        @Override
        public String getInventoryDisplayNameForGui()
        {
            return "Cosmetic Armor";
        }

        @Override
        public NBTBase getNbtData(EntityPlayer player)
        {
            if (ModConfigs.CosArmorKeepThroughDeath)
                return null;
            return SpecialInventoryHelper.getTagListFromIInventory(CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID()));
        }

        @Override
        public int getPriority()
        {
            return 0;
        }

        @Override
        public String getUniqueIdentifier()
        {
            return "CosmeticArmorReworkedInventory";
        }

        @Override
        public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce)
        {
            if (compound instanceof NBTTagList)
            {
                TransitionInventory graveItems = new TransitionInventory((NBTTagList) compound);
                IInventory currentInventory = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());

                for (int i = 0; i < graveItems.getSizeInventory(); i++)
                {
                    ItemStack graveItem = graveItems.getStackInSlot(i);
                    if (!graveItem.isEmpty())
                    {
                        ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

                        if (playerItem.isEmpty())
                        {
                            // No problem, just put the grave item in!
                            currentInventory.setInventorySlotContents(i, graveItem);
                        }
                        else if (shouldForce)
                        {
                            // Slot is blocked, but we're forcing the grave item into place.
                            currentInventory.setInventorySlotContents(i, graveItem);
                            SpecialInventoryHelper.dropItem(player, playerItem);
                        }
                        else
                        {
                            // Slot is blocked, but we're not forcing items in - drop the grave item
                            SpecialInventoryHelper.dropItem(player, graveItem);
                        }
                    }
                }
            }
        }

        @Override
        public boolean isOverwritable()
        {
            return false;
        }

        @Override
        public boolean pregrabLogic(EntityPlayer player)
        {
            return true; // No logic to stop graves
        }

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e)
    {
        try
        {
            if (Loader.isModLoaded("cosmeticarmorreworked") && Loader.isModLoaded("tombmanygraves2api") && Loader.isModLoaded("tombmanygraves"))
                new CosmeticArmorSpecialInventory();
        }
        catch (Throwable t)
        {
            System.err.println("Error loading CosmeticArmorReworked Integration for TombManyGraves2: " + t.getMessage());
            t.printStackTrace();
        }
    }

}
