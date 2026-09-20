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
import java.util.Random;

public class SpookyBypass implements ModInitializer {
    public static boolean enabled = false;
    public static float serverYaw, serverPitch;
    private static KeyBinding toggleKey;
    private static final Random RNG = new Random();
    private static double gcdFactor = 0.001D;

    @Override
    public void onInitialize() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle Aim", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "SpookyBypass"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (enabled) calibrateSens();
            }
            if (!enabled || mc.player == null || mc.world == null) return;

            Entity target = mc.world.getEntities().stream()
                .filter(e -> isValidTarget(mc, e))
                .min(Comparator.comparingDouble(e -> mc.player.squaredDistanceTo(e)))
                .orElse(null);

            if (target == null) return;
            if (!mc.options.keyAttack.isPressed()) return;

            Vec3d eye = mc.player.getCameraPosVec(1.0F);
            Vec3d tPos = target.getPos().add(0, target.getHeight() * 0.85D, 0);
            double dx = tPos.x - eye.x, dy = tPos.y - eye.y, dz = tPos.z - eye.z;
            double dist = Math.sqrt(dx * dx + dz * dz);

            float tYaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
            float tPitch = (float)-Math.toDegrees(Math.atan2(dy, dist));

            float dY = MathHelper.wrapDegrees(tYaw - mc.player.yaw);
            float dP = tPitch - mc.player.pitch;
            float factor = 0.3F + RNG.nextFloat() * 0.15F;

            serverYaw = quantize(mc.player.yaw + dY * factor);
            serverPitch = MathHelper.clamp(quantize(mc.player.pitch + dP * factor), -90F, 90F);

            float totalDelta = (float)Math.sqrt(dY*dY + dP*dP);
            if (totalDelta > 12F) {
                float scale = 12F / totalDelta;
                serverYaw = quantize(mc.player.yaw + dY * factor * scale);
                serverPitch = MathHelper.clamp(quantize(mc.player.pitch + dP * factor * scale), -90F, 90F);
            }
        });
    }

    private static void calibrateSens() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.options != null)
            gcdFactor = Math.max(mc.options.mouseSensitivity * 0.025D, 0.001D);
    }

    private static float quantize(float angle) {
        return (float)(Math.round(angle / gcdFactor) * gcdFactor);
    }

    private static boolean isValidTarget(MinecraftClient mc, Entity e) {
        if (e == null || e == mc.player || !(e instanceof LivingEntity)) return false;
        if (e.isDead() || ((LivingEntity)e).getHealth() <= 0) return false;
        if (mc.player.squaredDistanceTo(e) > 36.0D) return false;
        if (!mc.player.canSee(e)) return false;
        float[] rot = getRot(e, mc.player.getCameraPosVec(1.0F));
        if (Math.abs(MathHelper.wrapDegrees(rot[0] - mc.player.yaw)) > 45F) return false;
        return e instanceof PlayerEntity;
    }

    private static float[] getRot(Entity target, Vec3d eye) {
        Vec3d p = target.getPos().add(0, target.getHeight() * 0.85D, 0);
        double dx = p.x - eye.x, dy = p.y - eye.y, dz = p.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        return new float[]{
            (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0F),
            (float)-Math.toDegrees(Math.atan2(dy, dist))
        };
    }
}
