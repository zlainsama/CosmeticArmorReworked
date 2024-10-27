package lain.mods.cos.impl.inventory;

import com.mojang.datafixers.util.Pair;
import lain.mods.cos.impl.ModObjects;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ContainerCosArmor extends AbstractCraftingMenu {

    private static final Map<EquipmentSlot, ResourceLocation> TEXTURE_EMPTY_SLOTS = Map.of(EquipmentSlot.FEET, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, EquipmentSlot.LEGS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, EquipmentSlot.CHEST, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, EquipmentSlot.HEAD, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET);
    private static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private final Player player;

    public ContainerCosArmor(Inventory invPlayer, InventoryCosArmor invCosArmor, Player player, int windowId) {
        super(ModObjects.getTypeContainerCosArmor(), windowId, 2, 2);

        this.player = player;

        // Crafting
        this.addResultSlot(player, 154, 28);
        this.addCraftingGridSlots(98, 18);

        // NormalArmor
        for (int k = 0; k < 4; ++k) {
            final EquipmentSlot equipmentslottype = SLOT_IDS[k];
            addSlot(new Slot(invPlayer, 39 - k, 8, 8 + k * 18) {

                @Override
                public void setByPlayer(ItemStack pNewStack, ItemStack pOldStack) {
                    player.onEquipItem(equipmentslottype, pOldStack, pNewStack);
                    super.setByPlayer(pNewStack, pOldStack);
                }

                @Override
                public boolean mayPickup(Player playerIn) {
                    ItemStack itemstack = getItem();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE)) && super.mayPickup(playerIn);
                }

                @Override
                @Nullable
                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS.get(equipmentslottype));
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

        // PlayerInventory & HotBar
        addStandardInventorySlots(invPlayer, 8, 84);

        // OffHand
        addSlot(new Slot(invPlayer, 40, 77, 62) {

            @Override
            public void setByPlayer(ItemStack pNewStack, ItemStack pOldStack) {
                player.onEquipItem(EquipmentSlot.OFFHAND, pOldStack, pNewStack);
                super.setByPlayer(pNewStack, pOldStack);
            }

            @Override
            @Nullable
            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }

        });

        // CosmeticArmor
        for (int i = 0; i < 4; i++) {
            final EquipmentSlot equipmentslottype = SLOT_IDS[i];
            addSlot(new Slot(invCosArmor, 3 - i, 98 + i * 18, 62) {

                @Override
                @Nullable
                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS.get(equipmentslottype));
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

    // net.minecraft.world.inventory.InventoryMenu.slotChangedCraftingGrid()
    private static void slotChangedCraftingGrid(AbstractContainerMenu p_150547_, ServerLevel p_379963_, Player p_150549_, CraftingContainer p_150550_, ResultContainer p_150551_, @Nullable RecipeHolder<CraftingRecipe> p_345124_) {
        CraftingInput craftinginput = p_150550_.asCraftInput();
        ServerPlayer serverplayer = (ServerPlayer) p_150549_;
        ItemStack itemstack = ItemStack.EMPTY;
        Optional<RecipeHolder<CraftingRecipe>> optional = p_379963_.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftinginput, p_379963_, p_345124_);
        if (optional.isPresent()) {
            RecipeHolder<CraftingRecipe> recipeholder = optional.get();
            CraftingRecipe craftingrecipe = recipeholder.value();
            if (p_150551_.setRecipeUsed(serverplayer, recipeholder)) {
                ItemStack itemstack1 = craftingrecipe.assemble(craftinginput, p_379963_.registryAccess());
                if (itemstack1.isItemEnabled(p_379963_.enabledFeatures())) {
                    itemstack = itemstack1;
                }
            }
        }

        p_150551_.setItem(0, itemstack);
        p_150547_.setRemoteSlot(0, itemstack);
        serverplayer.connection.send(new ClientboundContainerSetSlotPacket(p_150547_.containerId, p_150547_.incrementStateId(), 0, itemstack));
    }

    @Override
    public void slotsChanged(Container inventoryIn) {
        if (player.level() instanceof ServerLevel level)
            slotChangedCraftingGrid(this, level, player, craftSlots, resultSlots, null);
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);

        resultSlots.clearContent();
        if (!playerIn.level().isClientSide)
            clearContainer(playerIn, craftSlots);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);

        if ((slot != null) && (slot.hasItem())) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();
            EquipmentSlot desiredSlot = player.getEquipmentSlotForItem(stack);

            if (slotIndex == 0) // CraftingResult
            {
                if (!moveItemStackTo(stack1, 9, 45, true))
                    return ItemStack.EMPTY;

                slot.onQuickCraft(stack1, stack);
            } else if ((slotIndex >= 1) && (slotIndex < 5)) // CraftingGrid
            {
                if (!moveItemStackTo(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            } else if ((slotIndex >= 5) && (slotIndex < 9)) // NormalArmor
            {
                if (!moveItemStackTo(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            } else if ((slotIndex >= 46) && (slotIndex < 50)) // CosmeticArmor
            {
                if (!moveItemStackTo(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            } else if (desiredSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && !slots.get(8 - desiredSlot.getIndex()).hasItem()) // ItemArmor - check NormalArmor slots
            {
                int j = 8 - desiredSlot.getIndex();

                if (!moveItemStackTo(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            } else if (desiredSlot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && !slots.get(49 - desiredSlot.getIndex()).hasItem()) // ItemArmor - check CosmeticArmor slots
            {
                int j = 49 - desiredSlot.getIndex();

                if (!moveItemStackTo(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            } else if ((slotIndex >= 9) && (slotIndex < 36)) // PlayerInventory
            {
                if (!moveItemStackTo(stack1, 36, 45, false))
                    return ItemStack.EMPTY;
            } else if ((slotIndex >= 36) && (slotIndex < 45)) // PlayerHotBar
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

            slot.onTake(player, stack1);
            if (slotIndex == 0)
                player.drop(stack1, false);
        }

        return stack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
        return slotIn.container != resultSlots && super.canTakeItemForPickAll(stack, slotIn);
    }

    @Override
    public Slot getResultSlot() {
        return slots.get(0);
    }

    @Override
    public List<Slot> getInputGridSlots() {
        return slots.subList(1, 5);
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    protected Player owner() {
        return player;
    }

}
