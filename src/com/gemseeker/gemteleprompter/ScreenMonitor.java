package com.gemseeker.gemteleprompter;

import java.awt.GraphicsDevice;

public class ScreenMonitor {

    private final GraphicsDevice graphicsDevice;
    private final String name;
    
    public ScreenMonitor(String name, GraphicsDevice graphicsDevice) {
        this.graphicsDevice = graphicsDevice;
        this.name = name;
    }

    public GraphicsDevice getGraphicsDevice() {
        return graphicsDevice;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
