package com.gemseeker.gemteleprompter.services;

import com.gemseeker.gemteleprompter.ScreenMonitor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author RAFIS-FRED
 */
public class LoadMonitorsService extends Service<List<ScreenMonitor>> {

    @Override
    protected Task<List<ScreenMonitor>> createTask() {
        return new Task<List<ScreenMonitor>>() {
            @Override
            protected List<ScreenMonitor> call() throws Exception {
                List<ScreenMonitor> monitors = new ArrayList<>();
            
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice devices[] = ge.getScreenDevices();

                for(int i=0; i<devices.length; i++) {
                    String name = "Monitor " + (i+1);
                    ScreenMonitor monitor = new ScreenMonitor(name, devices[i]);
                    monitors.add(monitor);
                }
                
                return monitors;
            }
        };
    }
    
}
