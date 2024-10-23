package lain.mods.cos.init.neoforge.mixins;

import lain.mods.cos.impl.client.PlayerRenderHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer {

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void CosArmor_onExtractRenderState(AbstractClientPlayer player, PlayerRenderState state, float partialTicks, CallbackInfo info) {
        PlayerRenderHandler.INSTANCE.onExtractPlayerRenderState(player, state, partialTicks);
    }

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void CosArmor_onFinishRenderState(AbstractClientPlayer player, PlayerRenderState state, float partialTicks, CallbackInfo info) {
        PlayerRenderHandler.INSTANCE.onFinishPlayerRenderState(player, state, partialTicks);
    }

}
