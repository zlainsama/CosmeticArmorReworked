package lain.mods.cos.impl.inventory;

import com.mojang.datafixers.util.Pair;
import lain.mods.cos.impl.ModObjects;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Optional;

public class ContainerCosArmor extends RecipeBookMenu<CraftingContainer> {

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private final Player player;

    private final CraftingContainer craftingInventory = new CraftingContainer(this, 2, 2);
    private final ResultContainer craftResultInventory = new ResultContainer();

    public ContainerCosArmor(Inventory invPlayer, InventoryCosArmor invCosArmor, Player player, int windowId) {
        super(ModObjects.getTypeContainerCosArmor(), windowId);

        this.player = player;

        // Crafting
        addSlot(new ResultSlot(player, craftingInventory, craftResultInventory, 0, 154, 28));
        for (int i = 0; i < 2; ++i)
            for (int j = 0; j < 2; ++j)
                addSlot(new Slot(craftingInventory, j + i * 2, 98 + j * 18, 18 + i * 18));

        // NormalArmor
        for (int k = 0; k < 4; ++k) {
            final EquipmentSlot equipmentslottype = VALID_EQUIPMENT_SLOTS[k];
            addSlot(new Slot(invPlayer, 39 - k, 8, 8 + k * 18) {

                @Override
                public boolean mayPickup(Player playerIn) {
                    ItemStack itemstack = getItem();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.mayPickup(playerIn);
                }

                @Override
                @Nullable
                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
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
                return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
            }

        });

        // CosmeticArmor
        for (int i = 0; i < 4; i++) {
            final EquipmentSlot equipmentslottype = VALID_EQUIPMENT_SLOTS[i];
            addSlot(new Slot(invCosArmor, 3 - i, 98 + i * 18, 62) {

                @Override
                @Nullable
                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
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
    private static void updateCrafting(AbstractContainerMenu menu, Level world, Player player, CraftingContainer craftingInventory, ResultContainer craftResultInventory) {
        if (!world.isClientSide) {
            ServerPlayer serverplayer = (ServerPlayer) player;
            ItemStack stack = ItemStack.EMPTY;
            Optional<CraftingRecipe> optionalrecipe = world.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInventory, world);
            if (optionalrecipe.isPresent()) {
                CraftingRecipe recipe = optionalrecipe.get();
                if (craftResultInventory.setRecipeUsed(world, serverplayer, recipe))
                    stack = recipe.assemble(craftingInventory, world.registryAccess());
            }

            craftResultInventory.setItem(0, stack);
            serverplayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, stack));
        }
    }

    @Override
    public boolean stillValid(Player playerIn) {
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
    public void fillCraftSlotsStackedContents(StackedContents arg0) {
        craftingInventory.fillStackedContents(arg0);
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int slotIndex) {
        return slotIndex != getResultSlotIndex();
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
    public boolean recipeMatches(Recipe<? super CraftingContainer> arg0) {
        return arg0.matches(craftingInventory, player.level);
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);

        craftResultInventory.clearContent();
        if (!playerIn.level.isClientSide)
            clearContainer(playerIn, craftingInventory);
    }

    @Override
    public void slotsChanged(Container inventoryIn) {
        updateCrafting(this, player.level, player, craftingInventory, craftResultInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);

        if ((slot != null) && (slot.hasItem())) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();
            EquipmentSlot desiredSlot = Mob.getEquipmentSlotForItem(stack);

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
            } else if (desiredSlot.getType() == EquipmentSlot.Type.ARMOR && !slots.get(8 - desiredSlot.getIndex()).hasItem()) // ItemArmor - check NormalArmor slots
            {
                int j = 8 - desiredSlot.getIndex();

                if (!moveItemStackTo(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            } else if (desiredSlot.getType() == EquipmentSlot.Type.ARMOR && !slots.get(49 - desiredSlot.getIndex()).hasItem()) // ItemArmor - check CosmeticArmor slots
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

}
