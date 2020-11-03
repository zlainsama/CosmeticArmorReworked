package lain.mods.cos.impl.inventory;

import com.mojang.datafixers.util.Pair;
import lain.mods.cos.impl.ModObjects;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;

public class ContainerCosArmor extends RecipeBookContainer<CraftingInventory> {

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

    private final PlayerEntity player;

    private final CraftingInventory craftingInventory = new CraftingInventory(this, 2, 2);
    private final CraftResultInventory craftResultInventory = new CraftResultInventory();

    public ContainerCosArmor(PlayerInventory invPlayer, InventoryCosArmor invCosArmor, PlayerEntity player, int windowId) {
        super(ModObjects.typeContainerCosArmor, windowId);

        this.player = player;

        // Crafting
        addSlot(new CraftingResultSlot(player, craftingInventory, craftResultInventory, 0, 154, 28));
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 2; ++j)
                addSlot(new Slot(craftingInventory, j + i * 2, 98 + j * 18, 18 + i * 18));

        // NormalArmor
        for (int k = 0; k < 4; ++k) {
            final EquipmentSlotType equipmentslottype = VALID_EQUIPMENT_SLOTS[k];
            addSlot(new Slot(invPlayer, 39 - k, 8, 8 + k * 18) {

                @Override
                public boolean canTakeStack(PlayerEntity playerIn) {
                    ItemStack itemstack = getStack();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
                }

                @Override
                @Nullable
                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getBackground() {
                    return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
                }

                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return stack.canEquip(equipmentslottype, player);
                }

            });
        }

        // PlayerInventory
        for (int l = 0; l < 3; ++l)
            for (int j1 = 0; j1 < 9; ++j1)
                addSlot(new Slot(invPlayer, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));

        // HotBar
        for (int i1 = 0; i1 < 9; ++i1)
            addSlot(new Slot(invPlayer, i1, 8 + i1 * 18, 142));

        // OffHand
        addSlot(new Slot(invPlayer, 40, 77, 62) {

            @Override
            @Nullable
            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getBackground() {
                return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
            }

        });

        // CosmeticArmor
        for (int i = 0; i < 4; i++) {
            final EquipmentSlotType equipmentslottype = VALID_EQUIPMENT_SLOTS[i];
            addSlot(new Slot(invCosArmor, 3 - i, 98 + i * 18, 62) {

                @Override
                @Nullable
                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getBackground() {
                    return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
                }

                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(ItemStack stack) {
                    return stack.canEquip(equipmentslottype, player);
                }

            });
        }
    }

    // net.minecraft.inventory.container.WorkbenchContainer.func_217066_a()
    private static void updateCrafting(int windowId, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftResultInventory craftResultInventory) {
        if (!world.isRemote) {
            ServerPlayerEntity serverplayer = (ServerPlayerEntity) player;
            ItemStack stack = ItemStack.EMPTY;
            Optional<ICraftingRecipe> optionalrecipe = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftingInventory, world);
            if (optionalrecipe.isPresent()) {
                ICraftingRecipe recipe = optionalrecipe.get();
                if (craftResultInventory.canUseRecipe(world, serverplayer, recipe))
                    stack = recipe.getCraftingResult(craftingInventory);
            }

            craftResultInventory.setInventorySlotContents(0, stack);
            serverplayer.connection.sendPacket(new SSetSlotPacket(windowId, 0, stack));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != craftResultInventory && super.canMergeSlot(stack, slotIn);
    }

    @Override
    public void clear() {
        craftResultInventory.clear();
        craftingInventory.clear();
    }

    @Override
    public void fillStackedContents(RecipeItemHelper arg0) {
        craftingInventory.fillStackedContents(arg0);
    }

    @Override
    public RecipeBookCategory func_241850_m() {
        return RecipeBookCategory.CRAFTING;
    }

    @Override
    public int getHeight() {
        return craftingInventory.getHeight();
    }

    @Override
    public int getOutputSlot() {
        return 0;
    }

    @Override
    public int getSize() {
        return 5;
    }

    @Override
    public int getWidth() {
        return craftingInventory.getWidth();
    }

    @Override
    public boolean matches(IRecipe<? super CraftingInventory> arg0) {
        return arg0.matches(craftingInventory, player.world);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        craftResultInventory.clear();
        if (!playerIn.world.isRemote)
            clearContainer(playerIn, playerIn.world, craftingInventory);
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        updateCrafting(this.windowId, player.world, player, craftingInventory, craftResultInventory);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int slotNumber) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotNumber);

        if ((slot != null) && (slot.getHasStack())) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            EquipmentSlotType desiredSlot = MobEntity.getSlotForItemStack(stack);

            if (slotNumber == 0) // CraftingResult
            {
                if (!mergeItemStack(stack1, 9, 45, true))
                    return ItemStack.EMPTY;

                slot.onSlotChange(stack1, stack);
            } else if ((slotNumber >= 1) && (slotNumber < 5)) // CraftingGrid
            {
                if (!mergeItemStack(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            } else if ((slotNumber >= 5) && (slotNumber < 9)) // NormalArmor
            {
                if (!mergeItemStack(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            } else if ((slotNumber >= 46) && (slotNumber < 50)) // CosmeticArmor
            {
                if (!mergeItemStack(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            } else if (desiredSlot.getSlotType() == EquipmentSlotType.Group.ARMOR && !inventorySlots.get(8 - desiredSlot.getIndex()).getHasStack()) // ItemArmor - check NormalArmor slots
            {
                int j = 8 - desiredSlot.getIndex();

                if (!mergeItemStack(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            } else if (desiredSlot.getSlotType() == EquipmentSlotType.Group.ARMOR && !inventorySlots.get(49 - desiredSlot.getIndex()).getHasStack()) // ItemArmor - check CosmeticArmor slots
            {
                int j = 49 - desiredSlot.getIndex();

                if (!mergeItemStack(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            } else if ((slotNumber >= 9) && (slotNumber < 36)) // PlayerInventory
            {
                if (!mergeItemStack(stack1, 36, 45, false))
                    return ItemStack.EMPTY;
            } else if ((slotNumber >= 36) && (slotNumber < 45)) // PlayerHotBar
            {
                if (!mergeItemStack(stack1, 9, 36, false))
                    return ItemStack.EMPTY;
            } else if (!mergeItemStack(stack1, 9, 45, false)) {
                return ItemStack.EMPTY;
            }

            if (stack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();

            if (stack1.getCount() == stack.getCount())
                return ItemStack.EMPTY;

            ItemStack stack2 = slot.onTake(player, stack1);

            if (slotNumber == 0)
                player.dropItem(stack2, false);
        }

        return stack;
    }

}
