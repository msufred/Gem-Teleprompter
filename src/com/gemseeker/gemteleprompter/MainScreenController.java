package com.gemseeker.gemteleprompter;

import com.gemseeker.gemteleprompter.services.ScreenPreview;
import com.gemseeker.gemteleprompter.services.LoadFontService;
import com.gemseeker.gemteleprompter.services.LoadMonitorsService;
import java.awt.AWTException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;

public class MainScreenController implements Initializable {
    
    // Input Group
    @FXML ComboBox fonts;
    @FXML ComboBox fontSizes;
    @FXML ToggleButton wrapText;
    @FXML TextArea textArea;
    
    // Output Group
    @FXML ChoiceBox monitors;
    @FXML ToggleButton showOutputBtn;
    @FXML ToggleButton flipTextBtn;
    @FXML ComboBox prompterFps;
    @FXML ComboBox outFonts;
    @FXML ComboBox outFontSizes;
    @FXML ToggleButton boldBtn;
    @FXML ToggleButton italicBtn;
    @FXML ToggleButton underlineBtn;
    @FXML ColorPicker backgroundColor;
    @FXML ColorPicker fontColor;
    @FXML Slider speedSlider;
    @FXML Slider scrollSlider;
    @FXML ToggleButton playBtn;
    @FXML Button scrollUp;
    @FXML Button scrollDown;
    @FXML Button resetBtn;
    
    // Preview Group
    @FXML StackPane imageHolder;
    @FXML ImageView imageView;
    @FXML ToggleButton previewBtn;
    @FXML Button mirrorBtn;
    @FXML ChoiceBox fpsList;
    
    private final ObservableList<Integer> fontSizeList = 
            FXCollections.observableArrayList(
                    14, 16, 18, 20, 24, 28, 30, 32, 34, 38, 40, 46, 48, 56, 60, 72, 80, 100
            );
    
    private final ObservableList<String> fontsList = FXCollections.observableArrayList();
    private final ObservableList<ScreenMonitor> screenMonitors = FXCollections.observableArrayList();
    
    private final SimpleStringProperty prompterText = new SimpleStringProperty();
    
    private boolean isShown = false;
    private boolean isPlaying = false;
    private boolean isPreviewing = false;
    private boolean isPreviewMirrored = false;
    
    private Output output;
    private ScreenMonitor outputMonitor;
    
    private String mFont;
    private int mFontSize = 14;
    
    private String mOutFont;
    private int mOutFontSize = 14;
    
    private ScreenPreview screenPreview;
    
    public MainScreenController() {
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComponents();
    }    
    
    public void setOutput(Output output) {
        this.output = output;
    }
    
    private void updateInputFont() {
        Platform.runLater(() -> {
            Font font;
            if (mFont != null) {
                font = new Font(mFont, mFontSize);
            } else {
                font = new Font(mFontSize);
            }
            textArea.setFont(font);
        });
    }
    
    /**
     * Displays the output UI.
     */
    public void showOutput() {
        if (output != null) {
            output.show();
            showOutputBtn.setText("Close Output");
            isShown = true;
        }
    }

    /**
     * Closes (if open) the output UI.
     */
    public void closeOutput() {
        if (output != null) {
            if(isPlaying) {
                pauseAnimation();
            }
            output.close();
            showOutputBtn.setText("Show Output");
            isShown = false;
        }
    }
    
    /**
     * Starts prompter animation (scroll animation). The animation will only
     * play if output UI is visible.
     */
    public void playAnimation() {
        if (!isPlaying && isShown) {
            output.start();
            playBtn.setText("Stop");
            isPlaying = true;
        }
    }

    /**
     * Pauses prompter animation (scroll animation).
     */
    public void pauseAnimation() {
        if (isPlaying && isShown) {
            output.stop();
            playBtn.setText("Play");
            isPlaying = false;
        }
    }
    
    public SimpleStringProperty getPrompterText() {
        return prompterText;
    }
    
    /**
     * Clean up method which handle logics when application is about to exit
     * or terminate. Stopping background processes etc should be terminated in
     * here.
     */
    public void onCloseRequest() {
        // if output is open close it
        if (isShown) {
            closeOutput();
        }
        
        // if screen preview is running stop it
        stopPreview();
    }
    
    private void startPreview() {
        if (!isPreviewing && screenPreview != null) {
            screenPreview.start();
            isPreviewing = true;
            previewBtn.setText("Stop Preview");
            disablePreviewActions(true);
        }
    }
    
    private void stopPreview() {
        if (isPreviewing && screenPreview != null) {
            screenPreview.stop();
            isPreviewing = false;
            previewBtn.setText("Start Preview");
            disablePreviewActions(false);
        }
    }
    
    private void disablePreviewActions(boolean disable) {
        monitors.setDisable(disable);
        fpsList.setDisable(disable);
    }
    
    // <editor-fold defaultstate="collapse" desc="Components & Initializations">
    
    private void initComponents() {
        // Input Group
        LoadFontService loadFontService = new LoadFontService();
        loadFontService.setOnSucceeded((event) -> {
            List<String> fontNames = (List<String>)event.getSource().getValue();
            fontsList.setAll(fontNames);
            
            fonts.setItems(fontsList);
            outFonts.setItems(fontsList);
        });
        loadFontService.start();
        
        fonts.valueProperty().addListener((o, oldValue, newValue) -> {
            mFont = newValue.toString();
            updateInputFont();
        });
        
        outFonts.valueProperty().addListener((o, oldValue, newValue) -> {
            mOutFont = newValue.toString();
            if (output != null) {
                output.setFontFamily(mOutFont);
            }
        });
        
        fontSizes.setItems(fontSizeList);
        outFontSizes.setItems(fontSizeList);
        
        fontSizes.valueProperty().addListener((observable, oldValue, newValue) -> {
            mFontSize = Integer.parseInt(newValue.toString());
            updateInputFont();
        });
        
        outFontSizes.valueProperty().addListener((observable, oldValue, newValue) -> {
            mOutFontSize = Integer.parseInt(newValue.toString());
            if (output != null) {
                output.setFontSize(mOutFontSize);
            }
        });
        
        wrapText.setOnAction(ev -> {
            if (textArea.isWrapText()) {
                textArea.setWrapText(false);
            } else {
                textArea.setWrapText(true);
            }
        });
        
        textArea.textProperty().addListener((o, oldValue, newValue) -> {
            prompterText.set(newValue);
        });
        
        LoadMonitorsService loadMonitorsService = new LoadMonitorsService();
        loadMonitorsService.setOnSucceeded((event) -> {
            List<ScreenMonitor> monitorsList = (List<ScreenMonitor>) event.getSource().getValue();
            screenMonitors.setAll(monitorsList);
            monitors.setItems(screenMonitors);
            if (screenMonitors.size() > 1) {
                monitors.getSelectionModel().select(screenMonitors.size() - 1);
            }
        });
        loadMonitorsService.start();
        
        monitors.valueProperty().addListener((observable, oldValue, newValue) -> {
            outputMonitor = (ScreenMonitor) newValue;
            if (screenPreview != null) {
                try {
                    screenPreview.setGraphicsDevice(outputMonitor.getGraphicsDevice());
                } catch (AWTException ex) {
                    System.err.println(ex);
                }
            }
        });
        
        showOutputBtn.setOnAction((event) -> {
            if (isShown) {
                closeOutput();
            } else {
                showOutput();
            }
        });
        
        flipTextBtn.setOnAction((event) -> {
            if (output != null) {
                output.flipText();
            }
        });
        
        prompterFps.setItems(FXCollections.observableArrayList(
                24, 30, 60, 100
        ));
        prompterFps.getEditor().addEventFilter(KeyEvent.KEY_TYPED, e -> {
            String numbers = "0123456789";
            if (!numbers.contains(e.getCharacter())) {
                e.consume();
            }
        });
        prompterFps.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (output != null) {
                output.setFps(Double.parseDouble(newValue.toString()));
            }
        });
        prompterFps.getSelectionModel().select(0);
        
        boldBtn.setOnAction((event) -> {
            if (output != null) {
                output.toggleBold(boldBtn.isSelected());
            }
        });
        
        italicBtn.setOnAction((event) -> {
            if (output != null) {
                output.toggleItalic(italicBtn.isSelected());
            }
        });
        
        underlineBtn.setOnAction((event) -> {
            if (output != null) {
                output.toggleUnderline(underlineBtn.isSelected());
            }
        });
        
        backgroundColor.valueProperty().addListener((o, oldValue, newValue) -> {
            output.setBackgroundColor(newValue);
        });
        
        fontColor.setValue(Color.BLACK);
        fontColor.valueProperty().addListener((o, oldValue, newValue) -> {
            output.setFontColor(newValue);
        });
        
        speedSlider.valueProperty().addListener((o, oldValue, newValue) -> {
            long value = Math.round(newValue.doubleValue());
            output.setSpeed(newValue.doubleValue());
        });
        
        scrollSlider.valueProperty().addListener((o, oldValue, newValue) -> {
            output.setScrollSpeed(newValue.doubleValue());
        });
        
        playBtn.setOnAction((event) -> {
            if (isPlaying) {
                pauseAnimation();
            } else {
                playAnimation();
            }
        });
        
        scrollUp.setOnAction(e -> {
            pauseAnimation();
            output.scrollUp();
        });
        
        scrollDown.setOnAction(e -> {
            pauseAnimation();
            output.scrollDown();
        });
        
        resetBtn.setOnAction(e -> {
            pauseAnimation();
            output.restart();
        });
        
        previewBtn.setOnAction((event) -> {
            if (screenPreview == null) {
                screenPreview = new ScreenPreview(this);
                try {
                    ScreenMonitor screenMonitor = (ScreenMonitor) monitors.getValue();
                    screenPreview.setGraphicsDevice(screenMonitor.getGraphicsDevice());
                } catch (AWTException ex) {
                    System.err.println(ex);
                }
            }
            
            if (isPreviewing) {
                stopPreview();
            } else {
                startPreview();
            }
        });
        
        mirrorBtn.setOnAction((event) -> {
            if (isPreviewMirrored) {
                imageView.setRotate(0);
            } else {
                imageView.setRotate(180);
            }
            isPreviewMirrored = !isPreviewMirrored;
        });
        
        imageView.setRotationAxis(Rotate.Y_AXIS);

        fpsList.setItems(FXCollections.observableArrayList(24, 25, 30, 60));
        fpsList.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (screenPreview != null) {
                double fps = Double.parseDouble(newValue.toString());
                screenPreview.setFps(fps);
            }
        });
        fpsList.getSelectionModel().select(0);
    }
    
    // </editor-fold>
    
    public ImageView getPreview() {
        return imageView;
    }
}
