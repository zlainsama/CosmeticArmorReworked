package lain.mods.cos.impl.client;

import com.mojang.blaze3d.platform.InputConstants;
import lain.mods.cos.impl.client.gui.GuiCosArmorInventory;
import lain.mods.cos.impl.network.payload.PayloadOpenCosArmorInventory;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Consumer;
/*    
在该代码示例中，KeyHandler类被定义为一个枚举，并且只有一个枚举量INSTANCE。
这样做的好处是：
    线程安全：Java枚举类型本身就是线程安全的，因为Java虚拟机在加载枚举时保证其线程安全性。这意味着你不需要额外的同步代码来保护单例实例。
    
    懒加载和急加载：枚举实例在枚举类型被加载时创建，这是急加载模式。如果你想使用懒加载，可以在枚举的构造函数中初始化单例实例。

    防止反射攻击：由于枚举类型的安全性和特殊性，它能够防止通过反射（Reflection）API创建额外的枚举实例，这有助于保持单例的性质。
    
    简洁性：使用枚举可以简化单例的实现，因为你不需要编写私有的构造函数、实例变量、公共的静态访问方法和双重检查锁定（double-checked locking）。
    
    序列化保证：枚举类型天生支持序列化，并且不需要担心序列化时破坏单例的问题，因为每次反序列化枚举时，都会返回相同的实例。

在KeyHandler枚举中，INSTANCE是KeyHandler的唯一实例，它被用来处理客户端的按键事件。这种方式确保了所有的按键事件都通过同一个KeyHandler实例来处理，从而保证了按键状态的一致性。
registerEvents方法用于注册事件监听器，registerKeyMappings方法用于注册按键映射，这些方法都是通过INSTANCE来调用的，这样确保了所有的注册都是通过同一个实例完成的。 */
public enum KeyHandler {//按键控制

    INSTANCE;

    private final Minecraft mc = Minecraft.getInstance();
    //这行代码声明了一个名为mc的Minecraft类型的私有 final 字段，并使用Minecraft.getInstance()方法获取当前Minecraft实例。
    //这个实例将被用于访问Minecraft的游戏状态和上下文。

    public KeyMapping keyOpenCosArmorInventory = new KeyMapping("cos.key.opencosarmorinventory", InputConstants.UNKNOWN.getValue(), "key.categories.inventory");
    //这行代码定义了一个名为keyOpenCosArmorInventory的KeyMapping对象。KeyMapping是Minecraft中用于绑定按键和功能的类。
    //这里创建了一个新的KeyMapping实例，用于打开Cos装甲库存

    /**
     * 这是一个处理客户端tick事件的方法。它检查事件是否在开始阶段，并且Minecraft窗口是否活跃。
     * 如果是，它将检查是否按下了keyOpenCosArmorInventory绑定的按键，并且当前屏幕不是GuiCosArmorInventory的实例。
     * 如果是这种情况，它将发送一个网络包到服务器，请求打开Cos装甲库存。
     *
     */
    private void handleClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase !=  TickEvent.Phase.START|| !mc.isWindowActive())
            return;
        if (keyOpenCosArmorInventory.consumeClick() && !(mc.screen instanceof GuiCosArmorInventory))
            PacketDistributor.SERVER.noArg().send(new PayloadOpenCosArmorInventory());
    }

    public void registerEvents() {//注册事件监听器
        NeoForge.EVENT_BUS.addListener(this::handleClientTick);
    }
    /**
     * 注册按键映射的方法。它接受一个Consumer<KeyMapping>类型的参数，该参数是一个函数接口，用于接受一个KeyMapping对象并执行某些操作。
     * 在这个方法中，它将keyOpenCosArmorInventory传递给register函数，以便将其注册到Minecraft的按键处理系统中。
     * 
     */
    public void registerKeyMappings(Consumer<KeyMapping> register) {
        register.accept(keyOpenCosArmorInventory);
    }

}
