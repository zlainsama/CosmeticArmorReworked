package lain.mods.cos.impl.client;

import com.google.common.collect.ImmutableSet;
import lain.mods.cos.impl.ModConfigs;
import lain.mods.cos.impl.ModObjects;
import lain.mods.cos.impl.client.gui.*;
import lain.mods.cos.impl.network.payload.PayloadOpenCosArmorInventory;
import lain.mods.cos.impl.network.payload.PayloadOpenNormalInventory;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;

public enum GuiHandler {
    //枚举类型 GuiHandler 
    //可以看到只有一个枚举常量 INSTANCE

    INSTANCE;

    public static final Set<Integer> ButtonIds = ImmutableSet.of(76, 77);//ButtonIds->不可变整数集合ImmutableSet-[76,77]

    private int lastLeft; //上一次窗口的左边的位置
    
    private boolean lastInventoryOpen; //上一次物品栏是否打开
    
    /**
    *这个方法主要是处理屏幕位置变化和创造模式物品栏的打开/关闭状态变化时，更新GUI的相关内容。这确保了当这些状态发生变化时，GUI能够正确地显示
    */
    private void handleGuiDrawPre(ScreenEvent.Render.Pre event) {//它是在处理一个ScreenEvent.Render.Pre事件
        if (event.getScreen() instanceof AbstractContainerScreen) {
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();//通过调用ScreenEvent.Render.Pre事件一个getScreen()方法来获取Screen（屏幕）

            if (lastLeft != screen.getGuiLeft()) {//判断Screen（屏幕）位置是否发生了改变
                int diffLeft = screen.getGuiLeft() - lastLeft;//计算差值diffLeft
                lastLeft = screen.getGuiLeft();//更新lastLeft的值
                screen.children().stream().filter(IShiftingWidget.class::isInstance).map(IShiftingWidget.class::cast).forEach(b -> b.shiftLeft(diffLeft));
                //上面每一个所有可移动的子窗口（IShiftingWidget.class 类字面常量）调用了shiftLeft()方法来更新了位置
                /*
                * screen.children() 获取屏幕上的所有子元素
                * .stream().filter(IShiftingWidget.class::isInstance)将获取到的子元素集合转换为流（Stream），并过滤流中的元素，只保留那些是IShiftingWidget实例的元素
                *     IShiftingWidget.class::isInstance是一个方法引用，它调用isInstance方法来检查对象是否为IShiftingWidget的一个实例
                * .map(IShiftingWidget.class::cast) map这个操作将流中的元素从它们原来的类型转换为IShiftingWidget类型【这个是安全的】
                * .forEach(b -> b.shiftLeft(diffLeft)); 对于通过lambada每个捕获，调用shiftLeft()方法来改变位置
                */
            }
            if (event.getScreen() instanceof CreativeModeInventoryScreen) {//检查当前Screen是否为CreativeModeInventoryScreen的实例
                boolean isInventoryOpen = ((CreativeModeInventoryScreen) event.getScreen()).isInventoryOpen();//这次的物品栏是否打开 ((CreativeModeInventoryScreen) event.getScreen())
                if (lastInventoryOpen != isInventoryOpen) {//判断上一次的物品栏打开和这次的物品栏打开是否不同
                    lastInventoryOpen = isInventoryOpen; //不同时更新lastInventoryOpen
                    screen.children().stream().filter(ICreativeInvWidget.class::isInstance).map(ICreativeInvWidget.class::cast).forEach(b -> b.onSelectedTabChanged(isInventoryOpen));
                    //调用onSelectedTabChanged()方法来通知所有相关的创造模式物品栏小部件[发送的是一个布尔值->代表物品栏是否打开]【同理】
                }
            }
        }
    }
    
    /**
    *事件处理方法，用于处理图形用户界面（GUI）初始化后的事件
    */
    private void handleGuiInitPost(ScreenEvent.Init.Post event) { //用于处理ScreenEvent.Init.Post事件，这个事件在屏幕初始化后触发
        if (event.getScreen() instanceof AbstractContainerScreen) { //检查当前屏幕是否是AbstractContainerScreen的实例
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen(); //（1）将事件中提供的屏幕对象转换为AbstractContainerScreen类型

            lastLeft = screen instanceof CreativeModeInventoryScreen ? 0 : screen.getGuiLeft(); //如果是screen的类是创造物品屏幕栏类，则lastLeft设置为0，否则则通过getGuiLeft()方法来获取值
            lastInventoryOpen = true;//指示已打开
        }

        if (event.getScreen() instanceof InventoryScreen || event.getScreen() instanceof GuiCosArmorInventory) {//检查当前屏幕是否是InventoryScreen或GuiCosArmorInventory的实例
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen(); //（2）将事件中提供的屏幕对象转换为AbstractContainerScreen类型

            if (!ModConfigs.CosArmorGuiButton_Hidden.get()) {如果当前屏幕是创造性模式库存屏幕的实例
                event.addListener(new GuiCosArmorButton( //将GuiCosArmorButton添加到事件监听器中
                        screen.getGuiLeft() + ModConfigs.CosArmorGuiButton_Left.get()/* 65 */,
                        screen.getGuiTop() + ModConfigs.CosArmorGuiButton_Top.get()/* 67 */,
                        10, 10,
                        event.getScreen() instanceof GuiCosArmorInventory ? //如果当前事件screen属于GuiCosArmorInventory类 
                                Component.translatable("cos.gui.buttonnormal") ://则使用"cos.gui.buttonnormal"作为按钮的语言键值
                                Component.translatable("cos.gui.buttoncos"),//否则使用"cos.gui.buttoncos"作为按钮的语言键值
                        button -> {
                            if (screen instanceof GuiCosArmorInventory) {
                                InventoryScreen newGui = new InventoryScreen(screen.getMinecraft().player);
                                InventoryScreenAccess.setXMouse(newGui, ((GuiCosArmorInventory) screen).oldMouseX);
                                InventoryScreenAccess.setYMouse(newGui, ((GuiCosArmorInventory) screen).oldMouseY);
                                screen.getMinecraft().setScreen(newGui);
                                PacketDistributor.SERVER.noArg().send(new PayloadOpenNormalInventory());
                            } else {
                                PacketDistributor.SERVER.noArg().send(new PayloadOpenCosArmorInventory());
                            }
                        },
                        null));
            }
            if (!ModConfigs.CosArmorToggleButton_Hidden.get()) { //模组配置里的设置CosArmorToggleButton_Hidden是否启用
                event.addListener(new GuiCosArmorToggleButton(
                        screen.getGuiLeft() + ModConfigs.CosArmorToggleButton_Left.get()/* 59 */,
                        screen.getGuiTop() + ModConfigs.CosArmorToggleButton_Top.get()/* 72 */,
                        5, 5,
                        Component.empty(),
                        PlayerRenderHandler.Disabled ? 1 : 0,
                        button -> {
                            PlayerRenderHandler.Disabled = !PlayerRenderHandler.Disabled;
                            ((GuiCosArmorToggleButton) button).state = PlayerRenderHandler.Disabled ? 1 : 0;
                        }));
            }
        } else if (event.getScreen() instanceof CreativeModeInventoryScreen) { //如果当前屏幕是创造性模式库存屏幕的实例
            AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();

            if (!ModConfigs.CosArmorCreativeGuiButton_Hidden.get()) { //模组配置里的设置CosArmorCreativeGuiButton_Hidden是否启用
                event.addListener(new GuiCosArmorButton(
                        /*screen.leftPos + */ModConfigs.CosArmorCreativeGuiButton_Left.get()/* 95 */,
                        screen.getGuiTop() + ModConfigs.CosArmorCreativeGuiButton_Top.get()/* 38 */,
                        10, 10,
                        Component.translatable("cos.gui.buttoncos"),
                        button -> {
                            PacketDistributor.SERVER.noArg().send(new PayloadOpenCosArmorInventory());
                        },
                        (button, isInventoryOpen) -> {
                            button.visible = isInventoryOpen;
                        }));
            }
        }
    }

    public void registerEvents() { //这是一个公共方法，用于注册事件监听器
        NeoForge.EVENT_BUS.addListener(this::handleGuiDrawPre);
        //这行代码使用NeoForge.EVENT_BUS（一个事件总线）来注册handleGuiDrawPre方法作为GUI绘制前的事件监听器。this::handleGuiDrawPre是一个方法引用，它指向当前对象的handleGuiDrawPre方法
        NeoForge.EVENT_BUS.addListener(this::handleGuiInitPost);
        //NeoForge.EVENT_BUS.addListener(this::handleGuiInitPost)：这行代码注册handleGuiInitPost方法作为GUI初始化后的事件监听器
        setupGuiFactory();
    }
    
    /** 
    * MenuScreens.register(ModObjects.getTypeContainerCosArmor(), GuiCosArmorInventory::new)：这行代码使用MenuScreens.register方法来注册一个GUI工厂。
    * ModObjects.getTypeContainerCosArmor()返回一个容器类型，GuiCosArmorInventory::new是一个方法引用，
    * 它指向GuiCosArmorInventory类的构造函数。这意味着当创建ModObjects.getTypeContainerCosArmor()类型的容器时，将使用GuiCosArmorInventory类的实例作为GUI屏幕
    */
    private void setupGuiFactory() {//这是一个私有方法，用于设置GUI工厂。GUI工厂用于创建和配置特定的GUI屏幕
        MenuScreens.register(ModObjects.getTypeContainerCosArmor(), GuiCosArmorInventory::new);

    }

}
