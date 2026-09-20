package com.fox.spookybypass;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.stream.StreamSupport;

public class SpookyBypass implements ModInitializer {
    private static KeyBinding guiKey;
    
    @Override
    public void onInitialize() {
        guiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Open Config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "SpookyBypass"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (guiKey.wasPressed()) {
                mc.openScreen(new ConfigScreen());
            }
            
            if (!Config.enabled || mc.player == null || mc.world == null) return;

            Entity target = StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(e -> isValidTarget(mc, e))
                .min(Comparator.comparingDouble(e -> mc.player.squaredDistanceTo(e)))
                .orElse(null);

            if (target == null) return;
            if (!mc.options.keyAttack.isPressed()) return;

            Vec3d eye = mc.player.getCameraPosVec(1.0F);
            Vec3d tPos = target.getPos().add(0, target.getHeight() * 0.85D, 0);
            double dx = tPos.x - eye.x, dy = tPos.y - eye.y, dz = tPos.z - eye.z;
            double dist = Math.sqrt(dx * dx + dz * dz);

            float targetYaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
            float targetPitch = (float)-Math.toDegrees(Math.atan2(dy, dist));

            float deltaYaw = MathHelper.wrapDegrees(targetYaw - mc.player.yaw);
            float deltaPitch = targetPitch - mc.player.pitch;

            float smoothFactor = Config.smoothness;
            float newYaw = mc.player.yaw + deltaYaw * smoothFactor;
            float newPitch = mc.player.pitch + deltaPitch * smoothFactor;
            newPitch = MathHelper.clamp(newPitch, -90F, 90F);

            float totalDelta = (float)Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            if (totalDelta > Config.maxRotationSpeed) {
                float scale = Config.maxRotationSpeed / totalDelta;
                newYaw = mc.player.yaw + deltaYaw * scale;
                newPitch = MathHelper.clamp(mc.player.pitch + deltaPitch * scale, -90F, 90F);
            }

            mc.player.yaw = newYaw;
            mc.player.pitch = newPitch;
        });
    }

    private static boolean isValidTarget(MinecraftClient mc, Entity e) {
        if (e == null || e == mc.player || !(e instanceof LivingEntity)) return false;
        if (!e.isAlive() || ((LivingEntity)e).getHealth() <= 0) return false;
        if (mc.player.squaredDistanceTo(e) > Config.maxDistance * Config.maxDistance) return false;
        if (!mc.player.canSee(e)) return false;
        
        Vec3d p = e.getPos().add(0, e.getHeight() * 0.85D, 0);
        Vec3d eye = mc.player.getCameraPosVec(1.0F);
        double dx = p.x - eye.x, dy = p.y - eye.y, dz = p.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        
        float targetYaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float targetPitch = (float)-Math.toDegrees(Math.atan2(dy, dist));
        
        float angleDiff = Math.abs(MathHelper.wrapDegrees(targetYaw - mc.player.yaw));
        if (angleDiff > Config.fovLimit) return false;
        if (Math.abs(targetPitch - mc.player.pitch) > 25F) return false;
        
        return e instanceof PlayerEntity;
    }
}
