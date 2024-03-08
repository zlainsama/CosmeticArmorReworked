package lain.mods.cos.impl.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;
/**这个类用于创建一个自定义的GUI按钮 */
public class GuiCosArmorButton extends Button implements IShiftingWidget, ICreativeInvWidget {
    //这行代码声明了GuiCosArmorButton类，它继承自Button类并实现了两个接口：IShiftingWidget和ICreativeInvWidget

    private final Minecraft mc = Minecraft.getInstance();
    //这行代码声明了一个名为mc的Minecraft类型的私有final字段，并使用Minecraft.getInstance()方法获取当前Minecraft实例。
    //这个实例将被用于访问Minecraft的游戏状态和上下文。
    
    private final BiConsumer<GuiCosArmorButton, Boolean> onCreativeTabChanged;
    //这行代码声明了一个名为onCreativeTabChanged的BiConsumer类型的私有final字段。BiConsumer是一个函数接口，它接受两个输入参数并执行某些操作。
    //在这个上下文中，onCreativeTabChanged可能用于处理创意模式标签页更改的事件。

    public GuiCosArmorButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, BiConsumer<GuiCosArmorButton, Boolean> onCreativeTabChanged) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        this.onCreativeTabChanged = onCreativeTabChanged;
    }
    //这是GuiCosArmorButton类的构造函数。
    //它接受按钮的位置、大小、显示的消息、点击事件处理函数以及创意模式标签页更改事件处理函数作为参数，并调用父类的构造函数来初始化按钮。

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        boolean state = isHoveredOrFocused();
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        graphics.blit(GuiCosArmorInventory.TEXTURE, getX(), getY(), state ? 10 : 0, 166, 10, 10);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (state)
            graphics.drawCenteredString(mc.font, getMessage(), getX() + 5, getY() + height, 0xffffff);
    }
    //这个方法覆盖了Button类的renderWidget方法，用于渲染按钮。它根据按钮的状态（悬停或聚焦）来决定渲染的纹理和文本。
    //它使用GuiCosArmorInventory.TEXTURE作为按钮的纹理，并根据鼠标位置和按钮状态来渲染不同的纹理部分。
    //如果按钮被悬停或聚焦，它还会绘制按钮的消息文本

    @Override
    public void shiftLeft(int diffLeft) {
        setX(getX() + diffLeft);
    }
    //这个方法实现了IShiftingWidget接口的shiftLeft方法。它用于将按钮向左移动指定的距离。

    @Override
    public void onSelectedTabChanged(boolean isInventoryOpen) {
        if (onCreativeTabChanged != null)
            onCreativeTabChanged.accept(this, isInventoryOpen);
    }
    //这个方法实现了ICreativeInvWidget接口的onSelectedTabChanged方法。它接受一个布尔值作为参数，指示库存是否打开。
    //如果onCreativeTabChanged不为空，它会调用onCreativeTabChanged来处理创造模式标签页更改事件。
}
