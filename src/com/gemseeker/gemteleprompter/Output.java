package com.gemseeker.gemteleprompter;

import com.gemseeker.gemteleprompter.services.PrompterAnimationService;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The output ui of the teleprompter.
 * 
 * @author Gem Seeker 06-15-2018
 */
public class Output extends Stage {

    private VBox root;
    private Text output;
    private StackPane stackPane;
    private Rotate textRotation;
    private Translate textTranslate;
    
    private final SimpleDoubleProperty fontSizeProperty = new SimpleDoubleProperty(24);
    private final SimpleStringProperty fontFamilyProperty = new SimpleStringProperty();
    
    private final Duration scrollDuration = new Duration(300);
    
    private double mSpeed = 1;
    private double mScrollSpeed = 50;
    private double mTranslateY = 0;
    
    private final MainScreenController main;
    
    private boolean isPlaying = false;
    private boolean isFlipped = false;
    private boolean isScrolling = false;
    
    private String fontFamily;
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isUnderlined = false;
    
    private boolean isPreview = false;
    
    private PrompterAnimationService animation;
    
    public Output(MainScreenController main) {
        this.main = main;
        initComponents();
        initShortcuts();
        animation = new PrompterAnimationService(this);
    }
    
    public void start() {
        animation.start();
        isPlaying = true;
    }
    
    public void stop() {
        animation.stop();
        isPlaying = false;
    }
    
    public void restart() {
        Platform.runLater(() -> {
            output.setTranslateY(0);
            mTranslateY = 0;
        });
    }
    
    public void scrollUp() {
        if (!isScrolling) {
            KeyValue keyValue = new KeyValue(output.translateYProperty(), mTranslateY + mScrollSpeed);
            KeyFrame keyFrame = new KeyFrame(scrollDuration, e -> {
                mTranslateY += mScrollSpeed;
                isScrolling = false;
            }, keyValue);
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(keyFrame);
            
            isScrolling = true;
            timeline.play();
        }
    }
    
    public void scrollDown() {
        if (!isScrolling) {
            KeyValue keyValue = new KeyValue(output.translateYProperty(), mTranslateY - mScrollSpeed); // 40 pixels
            KeyFrame keyFrame = new KeyFrame(scrollDuration, e -> {
                mTranslateY -= mScrollSpeed;
                isScrolling = false;
            }, keyValue);
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().add(keyFrame);
            
            isScrolling = true;
            timeline.play();
        }
    }
    
    public void flipText() {
        // TODO:
        if (isFlipped) {
            textRotation.setAngle(0);
            textTranslate.setX(0);
        } else {
            textRotation.setAngle(180);
            textTranslate.setX(-(stackPane.getWidth()));
        }
        isFlipped = !isFlipped;
    }
    
    public void setFontSize(double fontSize) {
        fontSizeProperty.set(fontSize);
    }
    
    public void setFontFamily(String fontFamily) {
        fontFamilyProperty.set(fontFamily);
    }
    
    public void toggleBold(boolean isBold) {
        this.isBold = isBold;
        updateFontStyle();
    }
    
    public void toggleItalic(boolean isItalic) {
        this.isItalic = isItalic;
        updateFontStyle();
    }
    
    public void toggleUnderline(boolean isUnderlined) {
        this.isUnderlined = isUnderlined;
        updateFontStyle();
    }
    
    public void updateFontStyle() {
        if (isBold && !isItalic && !isUnderlined) { // B
            Font b = Font.font(fontFamilyProperty.get(), FontWeight.BOLD, fontSizeProperty.get());
            output.setFont(b);
            if (output.isUnderline()) output.setUnderline(false);
        } else if (!isBold && isItalic && !isUnderlined) { // I
            Font i = Font.font(fontFamilyProperty.get(), FontPosture.ITALIC, fontSizeProperty.get());
            output.setFont(i);
            if (output.isUnderline()) output.setUnderline(false);
        } else if (!isBold && !isItalic && isUnderlined) { // U
            output.setUnderline(true);
        } else if (isBold && isItalic && !isUnderlined) { // BI
            Font bi = Font.font(fontFamilyProperty.get(), FontWeight.BOLD, FontPosture.ITALIC, fontSizeProperty.get());
            output.setFont(bi);
            if (output.isUnderline()) output.setUnderline(false);
        } else if (isBold && !isItalic && isUnderlined) { // BU
            Font bu = Font.font(fontFamilyProperty.get(), FontWeight.BOLD, fontSizeProperty.get());
            output.setFont(bu);
            output.setUnderline(true);
        } else if (!isBold && isItalic && isUnderlined) { // IU
            Font iu = Font.font(fontFamilyProperty.get(), FontPosture.ITALIC, fontSizeProperty.get());
            output.setFont(iu);
            output.setUnderline(true);
        } else if (isBold && isItalic && isUnderlined) { // BIU
            Font biu = Font.font(fontFamilyProperty.get(), FontWeight.BOLD, FontPosture.ITALIC, fontSizeProperty.get());
            output.setFont(biu);
            output.setUnderline(true);
        } else {
            Font reg = Font.font(fontFamilyProperty.get(), fontSizeProperty.get());
            output.setFont(reg);
            if (output.isUnderline()) output.setUnderline(false);
        }
    }
    
    public void setFps(double fps) {
        animation.setFps(fps);
    }
    
    public void setSpeed(double speed) {
        if (isPlaying) {
//            timer.stop();
            animation.stop();
        }
        
        mSpeed = speed;
        
        if (isPlaying) {
//            timer.start();
            animation.start();
        }
    }
    
    public void setScrollSpeed(double speed) {
        mScrollSpeed = speed;
    }
    
    public void setBackgroundColor(Color color) {
        root.setBackground(new Background(new BackgroundFill(color, null, null)));
    }
    
    public void setFontColor(Color color) {
        output.setFill(color);
    }
    
    /**
     * Updates the Y position of the text. This is executed in JavaFX Application
     * Thread.
     */
    public void updateTextPosition() {
        Platform.runLater(() -> {
            mTranslateY -= mSpeed;
            output.setTranslateY(mTranslateY);
        });
    }
    
    private void initComponents() {
        root = new VBox();
        root.setPadding(new Insets(16));
        output = new Text();
        output.wrappingWidthProperty().bind(root.widthProperty().subtract(32));
        output.setFont(new Font(fontSizeProperty.get()));
        output.textProperty().bind(main.getPrompterText());
        output.setFontSmoothingType(FontSmoothingType.LCD);

        stackPane = new StackPane();
        stackPane.getStyleClass().add("-fx-background-color: transparent;");
        stackPane.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(stackPane, Priority.ALWAYS);
        stackPane.getChildren().add(output);
        
        textRotation = new Rotate();
        textRotation.setAxis(Rotate.Y_AXIS);
        
        textTranslate = new Translate();
        
        stackPane.getTransforms().add(textRotation);
        output.getTransforms().add(textTranslate);
        root.getChildren().add(stackPane);
        
        fontSizeProperty.addListener((o) -> {
            updateFontStyle();
        });
        
        fontFamilyProperty.addListener((observable, oldValue, newValue) -> {
            updateFontStyle();
        });
        
        setScene(new Scene(root, 400, 600));
        
        setOnCloseRequest(e -> {
            if (isPlaying) stop();
            main.closeOutput();
        });
        
        setTitle("Output");
    }
    
    private void initShortcuts() {
        addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            // ESC: close this window
            if (ev.getCode() == KeyCode.ESCAPE) {
                main.closeOutput();
            }
            
            // SpaceBar: play/pause
            if (ev.getCode() == KeyCode.SPACE) {
                if (isPlaying) {
                    main.pauseAnimation();
                } else {
                    main.playAnimation();
                }
            }
            
            // UP: scroll up
            if (ev.getCode() == KeyCode.UP) {
                main.pauseAnimation();
                scrollUp();
            }
            
            // DOWN: scroll down
            if (ev.getCode() == KeyCode.DOWN) {
                main.pauseAnimation();
                scrollDown();
            }
            
            // CTRL + F: flip text
            if (ev.isControlDown() && ev.getCode() == KeyCode.F) {
                main.pauseAnimation();
                flipText();
            }
        });
    }
}
