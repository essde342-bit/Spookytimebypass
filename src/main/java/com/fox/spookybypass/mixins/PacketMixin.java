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
    private static final ThreadLocal<Boolean> REPLACING_PACKET =
        ThreadLocal.withInitial(() -> false);

    @Inject(method = "send(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        if (REPLACING_PACKET.get()
            || !(packet instanceof PlayerMoveC2SPacket)
            || !SpookyBypass.enabled) {
            return;
        }

        PlayerMoveC2SPacket movePacket = (PlayerMoveC2SPacket) packet;
        float fallback = 999999.0F;
        if (movePacket.getYaw(fallback) == fallback) return;

        float yaw = SpookyBypass.serverYaw;
        float pitch = SpookyBypass.serverPitch;
        if (yaw == 0 && pitch == 0) return;

        // Yarn 1.16.5 names the packet containing both position and rotation "Both".
        PlayerMoveC2SPacket modified = new PlayerMoveC2SPacket.Both(
            movePacket.getX(0), movePacket.getY(0), movePacket.getZ(0),
            yaw, pitch, movePacket.isOnGround()
        );

        REPLACING_PACKET.set(true);
        try {
            ((ClientConnection) (Object) this).send(modified);
            ci.cancel();
        } finally {
            REPLACING_PACKET.set(false);
        }
    }
}
