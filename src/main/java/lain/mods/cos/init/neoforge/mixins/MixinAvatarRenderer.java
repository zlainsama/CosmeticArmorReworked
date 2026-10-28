package lain.mods.cos.init.neoforge.mixins;

import lain.mods.cos.impl.client.PlayerRenderHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class MixinAvatarRenderer {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("HEAD"))
    private void CosArmor_onExtractRenderState(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo info) {
        if (entity instanceof AbstractClientPlayer player)
            PlayerRenderHandler.INSTANCE.onExtractPlayerRenderState(player, state, partialTicks);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("RETURN"))
    private void CosArmor_onFinishRenderState(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo info) {
        if (entity instanceof AbstractClientPlayer player)
            PlayerRenderHandler.INSTANCE.onFinishPlayerRenderState(player, state, partialTicks);
    }

}
