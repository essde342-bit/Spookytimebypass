package com.fox.spookybypass;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class ConfigScreen extends Screen {
    
    public ConfigScreen() {
        super(new LiteralText("SpookyBypass Config"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;
        int buttonWidth = 150;
        int buttonHeight = 20;
        int spacing = 25;
        
        // Toggle button
        this.addButton(new ButtonWidget(
            centerX - buttonWidth / 2, startY,
            buttonWidth, buttonHeight,
            new LiteralText(Config.enabled ? "§aENABLED" : "§cDISABLED"),
            button -> {
                Config.toggle();
                button.setMessage(new LiteralText(Config.enabled ? "§aENABLED" : "§cDISABLED"));
            }
        ));
        
        // Smoothness -
        this.addButton(new ButtonWidget(
            centerX - buttonWidth - 5, startY + spacing,
            70, buttonHeight,
            new LiteralText("Smooth -"),
            button -> {
                Config.smoothness = Math.max(0.05F, Config.smoothness - 0.05F);
            }
        ));
        
        // Smoothness +
        this.addButton(new ButtonWidget(
            centerX + 5, startY + spacing,
            70, buttonHeight,
            new LiteralText("Smooth +"),
            button -> {
                Config.smoothness = Math.min(0.5F, Config.smoothness + 0.05F);
            }
        ));
        
        // Speed -
        this.addButton(new ButtonWidget(
            centerX - buttonWidth - 5, startY + spacing * 2,
            70, buttonHeight,
            new LiteralText("Speed -"),
            button -> {
                Config.maxRotationSpeed = Math.max(2.0F, Config.maxRotationSpeed - 1.0F);
            }
        ));
        
        // Speed +
        this.addButton(new ButtonWidget(
            centerX + 5, startY + spacing * 2,
            70, buttonHeight,
            new LiteralText("Speed +"),
            button -> {
                Config.maxRotationSpeed = Math.min(20.0F, Config.maxRotationSpeed + 1.0F);
            }
        ));
        
        // FOV -
        this.addButton(new ButtonWidget(
            centerX - buttonWidth - 5, startY + spacing * 3,
            70, buttonHeight,
            new LiteralText("FOV -"),
            button -> {
                Config.fovLimit = Math.max(10.0F, Config.fovLimit - 5.0F);
            }
        ));
        
        // FOV +
        this.addButton(new ButtonWidget(
            centerX + 5, startY + spacing * 3,
            70, buttonHeight,
            new LiteralText("FOV +"),
            button -> {
                Config.fovLimit = Math.min(90.0F, Config.fovLimit + 5.0F);
            }
        ));
        
        // Distance -
        this.addButton(new ButtonWidget(
            centerX - buttonWidth - 5, startY + spacing * 4,
            70, buttonHeight,
            new LiteralText("Dist -"),
            button -> {
                Config.maxDistance = Math.max(3.0F, Config.maxDistance - 0.5F);
            }
        ));
        
        // Distance +
        this.addButton(new ButtonWidget(
            centerX + 5, startY + spacing * 4,
            70, buttonHeight,
            new LiteralText("Dist +"),
            button -> {
                Config.maxDistance = Math.min(10.0F, Config.maxDistance + 0.5F);
            }
        ));
        
        // Close button
        this.addButton(new ButtonWidget(
            centerX - buttonWidth / 2, startY + spacing * 5 + 10,
            buttonWidth, buttonHeight,
            new LiteralText("Close"),
            button -> {
                this.onClose();
            }
        ));
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        
        drawCenteredText(matrices, this.textRenderer, "§lSpookyBypass Config", this.width / 2, 20, 0xFFFFFF);
        
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;
        int spacing = 25;
        
        drawCenteredText(matrices, this.textRenderer, 
            String.format("Smooth: %.2f", Config.smoothness), 
            centerX, startY + spacing + 6, 0xAAAAAA);
        
        drawCenteredText(matrices, this.textRenderer, 
            String.format("Speed: %.1f", Config.maxRotationSpeed), 
            centerX, startY + spacing * 2 + 6, 0xAAAAAA);
        
        drawCenteredText(matrices, this.textRenderer, 
            String.format("FOV: %.0f", Config.fovLimit), 
            centerX, startY + spacing * 3 + 6, 0xAAAAAA);
        
        drawCenteredText(matrices, this.textRenderer, 
            String.format("Distance: %.1f", Config.maxDistance), 
            centerX, startY + spacing * 4 + 6, 0xAAAAAA);
        
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
}
