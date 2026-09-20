package com.fox.spookybypass;

public class Config {
    public static boolean enabled = false;
    public static float smoothness = 0.15F;
    public static float maxRotationSpeed = 8.0F;
    public static float fovLimit = 30.0F;
    public static float maxDistance = 6.0F;
    
    public static void toggle() {
        enabled = !enabled;
    }
}
