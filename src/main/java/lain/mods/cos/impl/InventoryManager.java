package lain.mods.cos.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lain.mods.cos.api.event.CosArmorDeathDrops;
import lain.mods.cos.impl.inventory.ContainerCosArmor;
import lain.mods.cos.impl.inventory.InventoryCosArmor;
import lain.mods.cos.impl.network.payload.PayloadSyncCosArmor;
import lain.mods.cos.impl.network.payload.PayloadSyncHiddenFlags;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;

public class InventoryManager {//物品栏管理类

    protected static final InventoryCosArmor Dummy = new InventoryCosArmor() {//匿名InventoryCosArmor类

        @Override
        @Nonnull
        //额外的物品 - 节点id 数量 <?>simulate
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;//空
        }

        @Override
        @Nonnull
        //获取对应节点的物品
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        @Nonnull
        //插入物品
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        //是否可见 - Modid  <?>identifier
        public boolean isHidden(String modid, String identifier) {
            return false;
        }

        @Override
        //根据节点判断是否为皮肤盔甲
        public boolean isSkinArmor(int slot) {
            return false;
        }

        @Override
        //根据节点来改变内容
        protected void onContentsChanged(int slot) {
        }

        @Override
        //<?>
        protected void onLoad() {
        }

        @Override
        //设置可见
        public boolean setHidden(String modid, String identifier, boolean set) {
            return false;
        }

        @Override
        //设置皮肤启用
        public void setSkinArmor(int slot, boolean enabled) {
        }

        @Override
        //设置格子中的物品
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        }

        @Override
        //设置内容改变更新监听
        public boolean setUpdateListener(ContentsChangeListener listener) {
            return false;
        }

        @Override
        //设置隐藏按钮状态更新监听
        public boolean setUpdateListener(HiddenFlagsChangeListener listener) {
            return false;
        }

    };
    //获取随机值
    protected static final Random RANDOM = new Random();
    //Cache信息<?>
    protected final LoadingCache<UUID, InventoryCosArmor> CommonCache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, InventoryCosArmor>() {

        @Override
        //根据UUID 加载类 返回一个InventoryCosArmor的实例
        public InventoryCosArmor load(UUID key) throws Exception {
            InventoryCosArmor inventory = new InventoryCosArmor();
            inventory.setUpdateListener((inv, slot) -> onInventoryChanged(key, inv, slot));
            inventory.setUpdateListener((inv, modid, identifier) -> onHiddenFlagsChanged(key, inv, modid, identifier));
            loadInventory(key, inventory);
            return inventory;
        }

    });
    //检查模组装备的问题 防止加载其它模组装备出现错误
    public static boolean checkIdentifier(String modid, String identifier) {
        if (modid == null || modid.isEmpty() || identifier == null || identifier.isEmpty() || !ModList.get().isLoaded(modid))
            return false;

        return false;//作者为什么在这里返回的都是false？？？（<?>）
    }
    //创建客户端容器（不支持的操作）
    public ContainerCosArmor createContainerClient(int windowId, Inventory invPlayer) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public InventoryCosArmor getCosArmorInventory(UUID uuid) {
        if (uuid == null)
            return Dummy;
        return CommonCache.getUnchecked(uuid);
    }

    @Nonnull
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid) {
        throw new UnsupportedOperationException();
    }

    protected File getDataFile(UUID uuid) {
        return new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile(), uuid + ".cosarmor");
    }

    private void handlePlayerDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getEntity().isEffectiveAi() && !event.getEntity().getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !ModConfigs.CosArmorKeepThroughDeath.get()) {
                InventoryCosArmor inv = getCosArmorInventory(event.getEntity().getUUID());
                if (NeoForge.EVENT_BUS.post(new CosArmorDeathDrops((Player) event.getEntity(), inv)).isCanceled())
                    return;
                for (int i = 0; i < inv.getSlots(); i++) {
                    ItemStack stack = inv.getStackInSlot(i).copy();
                    if (stack.isEmpty())
                        continue;

                    float fX = RANDOM.nextFloat() * 0.75F + 0.125F;
                    float fY = RANDOM.nextFloat() * 0.75F;
                    float fZ = RANDOM.nextFloat() * 0.75F + 0.125F;
                    while (!stack.isEmpty()) {
                        ItemEntity entity = new ItemEntity(event.getEntity().getCommandSenderWorld(), event.getEntity().getX() + (double) fX, event.getEntity().getY() + (double) fY, event.getEntity().getZ() + (double) fZ, stack.split(RANDOM.nextInt(21) + 10));
                        entity.setDeltaMovement(RANDOM.nextGaussian() * (double) 0.05F, RANDOM.nextGaussian() * (double) 0.05F + (double) 0.2F, RANDOM.nextGaussian() * (double) 0.05F);
                        event.getDrops().add(entity);
                    }

                    inv.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
        }
    }

    private void handlePlayerLoggedIn(PlayerLoggedInEvent event) {
        CommonCache.invalidate(event.getEntity().getUUID());
        getCosArmorInventory(event.getEntity().getUUID());

        if (event.getEntity() instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            for (ServerPlayer other : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (other == player)
                    continue;
                UUID uuid = other.getUUID();
                InventoryCosArmor inv = getCosArmorInventory(uuid);
                for (int i = 0; i < inv.getSlots(); i++)
                    PacketDistributor.PLAYER.with(player).send(new PayloadSyncCosArmor(uuid, inv, i));
                inv.forEachHidden((modid, identifier) -> PacketDistributor.PLAYER.with(player).send(new PayloadSyncHiddenFlags(uuid, inv, modid, identifier)));
            }
        }
    }

    private void handlePlayerLoggedOut(PlayerLoggedOutEvent event) {
        UUID uuid;
        InventoryCosArmor inv;
        if ((inv = CommonCache.getIfPresent(uuid = event.getEntity().getUUID())) != null) {
            saveInventory(uuid, inv);
            CommonCache.invalidate(uuid);
        }
    }

    private void handleRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("clearcosarmor").requires(s -> {
            return s.hasPermission(2);
        }).executes(s -> {
            int count = 0;
            ServerPlayer player = s.getSource().getPlayerOrException();
            InventoryCosArmor inv = getCosArmorInventory(player.getUUID());
            for (int i = 0; i < inv.getSlots(); i++)
                count += inv.extractItem(i, Integer.MAX_VALUE, false).getCount();

            final int result = count;
            s.getSource().sendSuccess(() -> Component.translatable("cos.command.clearcosarmor.success.single", result, player.getDisplayName()), true);
            return result;
        }).then(Commands.argument("targets", EntityArgument.players()).executes(s -> {
            int count = 0;
            Collection<ServerPlayer> players = EntityArgument.getPlayers(s, "targets");
            for (ServerPlayer player : players) {
                InventoryCosArmor inv = getCosArmorInventory(player.getUUID());
                for (int i = 0; i < inv.getSlots(); i++)
                    count += inv.extractItem(i, Integer.MAX_VALUE, false).getCount();
            }

            final int result = count;
            if (players.size() == 1)
                s.getSource().sendSuccess(() -> Component.translatable("cos.command.clearcosarmor.success.single", result, players.iterator().next().getDisplayName()), true);
            else
                s.getSource().sendSuccess(() -> Component.translatable("cos.command.clearcosarmor.success.multiple", result, players.size()), true);
            return result;
        })));

        if (!ModConfigs.CosArmorDisableCosHatCommand.get()) {
            event.getDispatcher().register(Commands.literal("coshat").requires(s -> {
                return s.hasPermission(0);
            }).executes(s -> {
                ServerPlayer player = s.getSource().getPlayerOrException();
                InventoryCosArmor inv = getCosArmorInventory(player.getUUID());
                ItemStack stack1 = player.getItemBySlot(EquipmentSlot.MAINHAND);
                ItemStack stack2 = inv.getStackInSlot(3);
                player.setItemSlot(EquipmentSlot.MAINHAND, stack2);
                inv.setStackInSlot(3, stack1);
                return 0;
            }));
        }
    }

    private void handleSaveToFile(PlayerEvent.SaveToFile event) {
        UUID uuid;
        InventoryCosArmor inv;
        if ((inv = CommonCache.getIfPresent(uuid = UUID.fromString(event.getPlayerUUID()))) != null)
            saveInventory(uuid, inv);
    }

    private void handleServerStopping(ServerStoppingEvent event) {
        ModObjects.logger.debug("Server is stopping... try to save all still loaded CosmeticArmor data");
        CommonCache.asMap().entrySet().forEach(e -> {
            ModObjects.logger.debug(e.getKey());
            saveInventory(e.getKey(), e.getValue());
        });
        CommonCache.invalidateAll();
    }
    //加载Inventory 
    protected void loadInventory(UUID uuid, InventoryCosArmor inventory) {
        if (inventory == Dummy)//空
            return;
        try {
            File file;
            if ((file = getDataFile(uuid)).exists())//读取Minecraft下的Data玩家文件 UUID+.dat
                inventory.deserializeNBT(NbtIo.read(file.toPath()));//反序化NBT
        } catch (Throwable t) {
            ModObjects.logger.fatal("Failed to load CosmeticArmor data", t);
        }
    }

    protected void onHiddenFlagsChanged(UUID uuid, InventoryCosArmor inventory, String modid, String identifier) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server.isDedicatedServer())
            PacketDistributor.ALL.noArg().send(new PayloadSyncHiddenFlags(uuid, inventory, modid, identifier));
        else
            server.getPlayerList().getPlayers().forEach(player -> PacketDistributor.PLAYER.with(player).send(new PayloadSyncHiddenFlags(uuid, inventory, modid, identifier)));
    }

    protected void onInventoryChanged(UUID uuid, InventoryCosArmor inventory, int slot) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server.isDedicatedServer())
            PacketDistributor.ALL.noArg().send(new PayloadSyncCosArmor(uuid, inventory, slot));
        else
            server.getPlayerList().getPlayers().forEach(player -> PacketDistributor.PLAYER.with(player).send(new PayloadSyncCosArmor(uuid, inventory, slot)));
    }

    public void registerEvents() {
        NeoForge.EVENT_BUS.addListener(this::handlePlayerDrops);
        NeoForge.EVENT_BUS.addListener(this::handlePlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(this::handlePlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(this::handleSaveToFile);
        NeoForge.EVENT_BUS.addListener(this::handleRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::handleServerStopping);
    }

    public void registerEventsClient() {
        throw new UnsupportedOperationException();
    }

    protected void saveInventory(UUID uuid, InventoryCosArmor inventory) {
        if (inventory == Dummy)
            return;
        try {
            NbtIo.write(inventory.serializeNBT(), getDataFile(uuid).toPath());
        } catch (Throwable t) {
            ModObjects.logger.fatal("Failed to save CosmeticArmor data", t);
        }
    }

}
