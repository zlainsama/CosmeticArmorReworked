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
                public boolean mayPickup(PlayerEntity playerIn) {
                    ItemStack itemstack = getItem();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(playerIn);
                }

                @Override
                @Nullable
                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(PlayerContainer.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
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
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
            }

        });

        // CosmeticArmor
        for (int i = 0; i < 4; i++) {
            final EquipmentSlotType equipmentslottype = VALID_EQUIPMENT_SLOTS[i];
            addSlot(new Slot(invCosArmor, 3 - i, 98 + i * 18, 62) {

                @Override
                @Nullable
                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(PlayerContainer.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(equipmentslottype, player);
                }

            });
        }
    }

    // net.minecraft.inventory.container.WorkbenchContainer.func_217066_a()
    private static void updateCrafting(int windowId, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftResultInventory craftResultInventory) {
        if (!world.isClientSide) {
            ServerPlayerEntity serverplayer = (ServerPlayerEntity) player;
            ItemStack stack = ItemStack.EMPTY;
            Optional<ICraftingRecipe> optionalrecipe = world.getServer().getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, craftingInventory, world);
            if (optionalrecipe.isPresent()) {
                ICraftingRecipe recipe = optionalrecipe.get();
                if (craftResultInventory.setRecipeUsed(world, serverplayer, recipe))
                    stack = recipe.assemble(craftingInventory);
            }

            craftResultInventory.setItem(0, stack);
            serverplayer.connection.send(new SSetSlotPacket(windowId, 0, stack));
        }
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        return slotIn.container != craftResultInventory && super.canTakeItemForPickAll(stack, slotIn);
    }

    @Override
    public void clearCraftingContent() {
        craftResultInventory.clearContent();
        craftingInventory.clearContent();
    }

    @Override
    public void fillCraftSlotsStackedContents(RecipeItemHelper arg0) {
        craftingInventory.fillStackedContents(arg0);
    }

    @Override
    public RecipeBookCategory getRecipeBookType() {
        return RecipeBookCategory.CRAFTING;
    }

    @Override
    public int getGridHeight() {
        return craftingInventory.getHeight();
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getSize() {
        return 5;
    }

    @Override
    public int getGridWidth() {
        return craftingInventory.getWidth();
    }

    @Override
    public boolean recipeMatches(IRecipe<? super CraftingInventory> arg0) {
        return arg0.matches(craftingInventory, player.level);
    }

    @Override
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        craftResultInventory.clearContent();
        if (!playerIn.level.isClientSide)
            clearContainer(playerIn, playerIn.level, craftingInventory);
    }

    @Override
    public void slotsChanged(IInventory inventoryIn) {
        updateCrafting(this.containerId, player.level, player, craftingInventory, craftResultInventory);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int slotNumber) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(slotNumber);

        if ((slot != null) && (slot.hasItem())) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();
            EquipmentSlotType desiredSlot = MobEntity.getEquipmentSlotForItem(stack);

            if (slotNumber == 0) // CraftingResult
            {
                if (!moveItemStackTo(stack1, 9, 45, true))
                    return ItemStack.EMPTY;

                slot.onQuickCraft(stack1, stack);
            } else if ((slotNumber >= 1) && (slotNumber < 5)) // CraftingGrid
            {
                if (!moveItemStackTo(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            } else if ((slotNumber >= 5) && (slotNumber < 9)) // NormalArmor
            {
                if (!moveItemStackTo(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            } else if ((slotNumber >= 46) && (slotNumber < 50)) // CosmeticArmor
            {
                if (!moveItemStackTo(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            } else if (desiredSlot.getType() == EquipmentSlotType.Group.ARMOR && !slots.get(8 - desiredSlot.getIndex()).hasItem()) // ItemArmor - check NormalArmor slots
            {
                int j = 8 - desiredSlot.getIndex();

                if (!moveItemStackTo(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            } else if (desiredSlot.getType() == EquipmentSlotType.Group.ARMOR && !slots.get(49 - desiredSlot.getIndex()).hasItem()) // ItemArmor - check CosmeticArmor slots
            {
                int j = 49 - desiredSlot.getIndex();

                if (!moveItemStackTo(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            } else if ((slotNumber >= 9) && (slotNumber < 36)) // PlayerInventory
            {
                if (!moveItemStackTo(stack1, 36, 45, false))
                    return ItemStack.EMPTY;
            } else if ((slotNumber >= 36) && (slotNumber < 45)) // PlayerHotBar
            {
                if (!moveItemStackTo(stack1, 9, 36, false))
                    return ItemStack.EMPTY;
            } else if (!moveItemStackTo(stack1, 9, 45, false)) {
                return ItemStack.EMPTY;
            }

            if (stack1.isEmpty())
                slot.set(ItemStack.EMPTY);
            else
                slot.setChanged();

            if (stack1.getCount() == stack.getCount())
                return ItemStack.EMPTY;

            ItemStack stack2 = slot.onTake(player, stack1);

            if (slotNumber == 0)
                player.drop(stack2, false);
        }

        return stack;
    }

}
