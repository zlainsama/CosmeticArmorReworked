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
    //创建（在客户端）容器（不支持的操作）
    public ContainerCosArmor createContainerClient(int windowId, Inventory invPlayer) {
        throw new UnsupportedOperationException();
    }
    //uuid为空就返回一个非正常实例Dummy
    @Nonnull
    public InventoryCosArmor getCosArmorInventory(UUID uuid) {
        if (uuid == null)
            return Dummy;
        return CommonCache.getUnchecked(uuid);
    }
    //不支持的操作（在客户端）
    @Nonnull
    public InventoryCosArmor getCosArmorInventoryClient(UUID uuid) {
        throw new UnsupportedOperationException();
    }
    //获取文件
    protected File getDataFile(UUID uuid) {
        return new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile(), uuid + ".cosarmor");
    }
    //处理物品掉落逻辑
    private void handlePlayerDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player) {
            //通过三个条件来决定是否会掉落物品 1.实体有 有效的 AI 2.没有开启 死亡保持物品 规则 3.在配置文件中没有设保留物品的配置为True
            if (event.getEntity().isEffectiveAi() && !event.getEntity().getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !ModConfigs.CosArmorKeepThroughDeath.get()) {
                InventoryCosArmor inv = getCosArmorInventory(event.getEntity().getUUID());//获取实体的CosArmor 的inv
                if (NeoForge.EVENT_BUS.post(new CosArmorDeathDrops((Player) event.getEntity(), inv)).isCanceled())
                    //先发布一个 CosArmorDeathDrops 类型的事件，表示某个玩家死亡并且掉落了特殊的物品，
                    //然后检查该事件是否被取消了（返回一个布尔值作为if判断依据，所以判断的是事件是否取消）
                    return;
                for (int i = 0; i < inv.getSlots(); i++) {//没有被取消的逻辑->对应的是物品的掉落机制 这里首先是遍历每一个CosInventory的Solt
                    ItemStack stack = inv.getStackInSlot(i).copy();//复制槽中的物品给stack
                    if (stack.isEmpty())
                        continue;//如果stack为空，则跳过这个slot，到下一个slotF

                    float fX = RANDOM.nextFloat() * 0.75F + 0.125F;//Fx
                    float fY = RANDOM.nextFloat() * 0.75F;//Fy
                    float fZ = RANDOM.nextFloat() * 0.75F + 0.125F;//Fz
                    while (!stack.isEmpty()) {//当stack非空，首先创建一个Item实体entity 给实体设置运动向量（物品掉落的运动状态），然后再将生成的物品实体添加到事件的掉落列表中 
                        ItemEntity entity = new ItemEntity(event.getEntity().getCommandSenderWorld(), event.getEntity().getX() + (double) fX, event.getEntity().getY() + (double) fY, event.getEntity().getZ() + (double) fZ, stack.split(RANDOM.nextInt(21) + 10));
                        entity.setDeltaMovement(RANDOM.nextGaussian() * (double) 0.05F, RANDOM.nextGaussian() * (double) 0.05F + (double) 0.2F, RANDOM.nextGaussian() * (double) 0.05F);
                        event.getDrops().add(entity);//将生成的物品实体添加到事件的掉落列表中
                    }

                    inv.setStackInSlot(i, ItemStack.EMPTY);//将当前槽位的物品堆栈设置为空
                }
            }
        }
    }
    //处理玩家登陆的逻辑
    private void handlePlayerLoggedIn(PlayerLoggedInEvent event) {
        CommonCache.invalidate(event.getEntity().getUUID());//调用了一个方法来清除缓存中与当前登录玩家相关的数据
        getCosArmorInventory(event.getEntity().getUUID());//这个方法调用获取了当前登录玩家的装备清单

        if (event.getEntity() instanceof ServerPlayer) {//检查当前登录的实体是否是服务器玩家
            ServerPlayer player = (ServerPlayer) event.getEntity();//向下转型
            for (ServerPlayer other : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {//遍历当前服务器中所有的玩家
                if (other == player)//在遍历玩家时，跳过当前登录的玩家
                    continue;
                UUID uuid = other.getUUID();
                InventoryCosArmor inv = getCosArmorInventory(uuid);//获取当前遍历到的玩家的装备清单信息
                for (int i = 0; i < inv.getSlots(); i++)
                    PacketDistributor.PLAYER.with(player).send(new PayloadSyncCosArmor(uuid, inv, i));
                inv.forEachHidden((modid, identifier) -> PacketDistributor.PLAYER.with(player).send(new PayloadSyncHiddenFlags(uuid, inv, modid, identifier)));
            //遍历当前玩家的装备清单中的所有槽位，发送同步消息给当前登录的玩家，确保登录后能够同步其他玩家的装备信息
            }
        }
    }
    //处理玩家登出的逻辑
    private void handlePlayerLoggedOut(PlayerLoggedOutEvent event) {
        UUID uuid;
        InventoryCosArmor inv;
        if ((inv = CommonCache.getIfPresent(uuid = event.getEntity().getUUID())) != null) {
            saveInventory(uuid, inv);//保存
            CommonCache.invalidate(uuid);
        }
    }
    //处理注册指令的逻辑（就是在注册指令，很简单的，不做解释）
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

        if (!ModConfigs.CosArmorDisableCosHatCommand.get()) {//配置
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
    //保存文件处理逻辑
    private void handleSaveToFile(PlayerEvent.SaveToFile event) {
        UUID uuid;
        InventoryCosArmor inv;
        if ((inv = CommonCache.getIfPresent(uuid = UUID.fromString(event.getPlayerUUID()))) != null)
            saveInventory(uuid, inv);
    }
    //当服务器停止时逻辑
    private void handleServerStopping(ServerStoppingEvent event) {
        ModObjects.logger.debug("Server is stopping... try to save all still loaded CosmeticArmor data");
        CommonCache.asMap().entrySet().forEach(e -> {
            ModObjects.logger.debug(e.getKey());
            saveInventory(e.getKey(), e.getValue());
            /*
                CommonCache.asMap().entrySet().forEach(e -> { ... });//这个语句是从CommonCache中获取所有的缓存条目，并遍历每一个条目
                
                    ModObjects.logger.debug(e.getKey());//用于调试和日志记录

                    saveInventory(e.getKey(), e.getValue());//调用一个方法来保存当前迭代条目对应的值，即该玩家的 CosmeticArmor 数据。
                    
                    e.getKey() 返回当前条目的键，通常是玩家的 UUID，
                    e.getValue() 返回当前条目的值，即该玩家的 CosmeticArmor 数据。

            */
        });
        CommonCache.invalidateAll();//清空所有缓存
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
            ModObjects.logger.fatal("Failed to load CosmeticArmor data", t);//失败抛出异常
        }
    }

    protected void onHiddenFlagsChanged(UUID uuid, InventoryCosArmor inventory, String modid, String identifier) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server.isDedicatedServer())//是否为独立服务器模式
            PacketDistributor.ALL.noArg().send(new PayloadSyncHiddenFlags(uuid, inventory, modid, identifier));
        else//在非独立服务器模式下，遍历所有在线的玩家，并向每个玩家发送一个同步消息，以通知它们玩家的隐藏标志发生了变化。这里使用 ‘PacketDistributor.PLAYER.with(player)’ 来表示将消息发送给指定玩家
            server.getPlayerList().getPlayers().forEach(player -> PacketDistributor.PLAYER.with(player).send(new PayloadSyncHiddenFlags(uuid, inventory, modid, identifier)));
        /*
            "独立服务器模式" 是指在 Minecraft 或类似游戏中运行一个独立的服务器程序，允许多个玩家通过网络连接一起游玩游戏。
            这种模式下，服务器运行在一个独立的进程中，而不是嵌入到玩家的游戏客户端中。
        */
    }
    //这个同上（不作解释）
    protected void onInventoryChanged(UUID uuid, InventoryCosArmor inventory, int slot) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server.isDedicatedServer())
            PacketDistributor.ALL.noArg().send(new PayloadSyncCosArmor(uuid, inventory, slot));
        else
            server.getPlayerList().getPlayers().forEach(player -> PacketDistributor.PLAYER.with(player).send(new PayloadSyncCosArmor(uuid, inventory, slot)));
    }
    //注册事件（添加进了Neoforge的监听事件）
    public void registerEvents() {
        NeoForge.EVENT_BUS.addListener(this::handlePlayerDrops);
        NeoForge.EVENT_BUS.addListener(this::handlePlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(this::handlePlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(this::handleSaveToFile);
        NeoForge.EVENT_BUS.addListener(this::handleRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::handleServerStopping);
    }
    //不支持的操作
    public void registerEventsClient() {
        throw new UnsupportedOperationException();
    }
    /*保存数据，通过NbtIO来写入NBT文件*/
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
