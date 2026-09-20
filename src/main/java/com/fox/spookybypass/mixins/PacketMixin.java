package com.fox.spookybypass.mixins;

import com.fox.spookybypass.SpookyBypass;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class PacketMixin {
    
    @Inject(method = "send(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        if (!(packet instanceof PlayerMoveC2SPacket)) return;
        if (!SpookyBypass.enabled) return;
        
        PlayerMoveC2SPacket movePacket = (PlayerMoveC2SPacket) packet;
        if (!movePacket.changesLook()) return;
        
        float yaw = SpookyBypass.serverYaw;
        float pitch = SpookyBypass.serverPitch;
        if (yaw == 0 && pitch == 0) return;
        
        PlayerMoveC2SPacket modified = new PlayerMoveC2SPacket(
            movePacket.getX(0), movePacket.getY(0), movePacket.getZ(0),
            yaw, pitch, movePacket.isOnGround()
        );
        
        try {
            ((ClientConnection)(Object)this).send(modified);
            ci.cancel();
        } catch (Exception e) {}
    }
}
