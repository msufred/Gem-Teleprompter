package com.gemseeker.gemteleprompter.services;

import com.gemseeker.gemteleprompter.MainScreenController;
import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

public class ScreenPreview implements Runnable {

    private boolean isRunning = false;
    private boolean isRendering = false;
    private double fps = 24D;
    
    // the BufferedImage to be rendered every *** frames per second
    private BufferedImage frameImage;
    
    private GraphicsDevice graphicsDevice;
    private Robot captureRobot;
    
    private final MainScreenController main;
    
    public ScreenPreview(MainScreenController main) {
        this.main = main;
    }
    
    public void setGraphicsDevice(GraphicsDevice graphicsDevice) throws AWTException{
        captureRobot = new Robot(graphicsDevice);
        this.graphicsDevice = graphicsDevice;
    }
    
    public void setFps(double fps) {
        this.fps = fps;
    }
    
    public synchronized void start() {
        // System.out.println("starting screen preview...");
        new Thread(this).start();
        isRunning = true;
    }
    
    public synchronized void stop() {
        isRunning = false;
        // System.out.println("screen preview stopped...");
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1_000_000_000/fps;
        
        int ticks = 0;
        int frames = 0;
        
        long lastTimer = System.currentTimeMillis();
        double delta = 0;
        
        while(isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            boolean doRender = false;
            
            while (delta >= 1) {
                ticks++;
                update();
                delta -= 1;
                doRender = true;
            }
            
            if (doRender) {
                if(!isRendering) {
                    frames++;
                    render();
                }
            }
            
            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                // System.out.println("Preview >> frames: " + frames + ", ticks: " + ticks);
                ticks = 0;
                frames = 0;
            }
        }
    }
    
    private void update() {
        // do updates
    }
    
    private void render() {
        // render
        if (graphicsDevice != null & captureRobot != null) {
            isRendering = true;
            Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
            frameImage = captureRobot.createScreenCapture(bounds);
            Platform.runLater(() -> {
                main.getPreview().setImage(SwingFXUtils.toFXImage(frameImage, null));
                frameImage = null;
                isRendering = false;
            });
        }
    }
}
