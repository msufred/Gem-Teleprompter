package com.gemseeker.gemteleprompter.services;

import com.gemseeker.gemteleprompter.Output;

/**
 * A subclass of Runnable class that renders/updates prompter text animation
 * n frames per second. This class uses a simple game loop logic to control
 * the number of update and render per second.
 * 
 * @author RAFIS-FRED
 */
public class PrompterAnimationService implements Runnable {
    
    private boolean isRunning = false;
    private double fps = 60D;
    
    private final Output output;
    
    public PrompterAnimationService(Output output) {
        this.output = output;
    }
    
    public void setFps(double fps) {
        this.fps = fps;
    }
    
    public synchronized void start() {
        System.out.println("starting prompter animation...");
        new Thread(this).start();
        isRunning = true;
    }
    
    public synchronized void stop() {
        isRunning = false;
        System.out.println("prompter animation stopped...");
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
                frames++;
                render();
            }
            
            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                System.out.println("Prompter >> frames: " + frames + ", ticks: " + ticks);
                ticks = 0;
                frames = 0;
            }
        }
    }
    
    private void update() {
        
    }
    
    private void render() {
        output.updateTextPosition();
    }
}
