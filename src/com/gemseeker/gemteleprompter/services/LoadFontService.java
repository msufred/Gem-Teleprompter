package com.gemseeker.gemteleprompter.services;

import java.util.List;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.text.Font;

/**
 *
 * @author RAFIS-FRED
 */
public class LoadFontService extends Service<List<String>> {

    @Override
    protected Task<List<String>> createTask() {
        return new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                return Font.getFamilies();
            }
        };
    }
    
}
