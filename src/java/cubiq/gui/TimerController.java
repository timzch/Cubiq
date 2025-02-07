package cubiq.gui;

import cubiq.models.GuiModel;
import cubiq.processing.MathUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.opencv.core.Mat;

import javax.swing.*;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;


public class TimerController implements Observer {

    private GuiModel guiModel;

    private long before, after;
    private boolean isRunning = false;
    private Timeline stopTimeline;
    @FXML
    private Text actualTimeText, bestTimeText, bestTime;
    @FXML
    private ProgressBar progBarGauss, progBarBlur, progBar;

    private void initializeTimerController() {

        actualTimeText.setFont(guiModel.getBender());
        actualTimeText.setStyle("-fx-font-size: 100");
        actualTimeText.setFontSmoothingType(FontSmoothingType.LCD);

        bestTimeText.setFont(guiModel.getBender());
        bestTimeText.setStyle("-fx-font-size: 15");
        bestTimeText.setFontSmoothingType(FontSmoothingType.LCD);
        bestTime.setFont(guiModel.getBender());
        bestTime.setStyle("-fx-font-size: 15");
        bestTime.setFontSmoothingType(FontSmoothingType.LCD);

        double bT = 60.0;
       // progBar.setProgress(elapsedTime-(bT / 100));
        progBarBlur = progBar;
        progBarGauss = progBar;
    }

    private void initStopwatchAnimation() {
        stopTimeline = new Timeline();
        stopTimeline.setCycleCount(Timeline.INDEFINITE);
        long startTime = System.currentTimeMillis();
        stopTimeline.getKeyFrames().add(
                new KeyFrame(new Duration(50), e -> actualTimeText.setText(timeConverter(System.currentTimeMillis()-startTime)))
        );
        stopTimeline.play();
    }

    private String timeConverter(long time) {
        Date date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat("mm:ss.S");
        return dateFormat.format(date).substring(0, 7);
    }

    @Override
    public void update(Observable o, Object arg) {
        switch ((String) arg) {
            case "initGuiElements":
                initializeTimerController();
                initStopwatchAnimation();
                break;
        }
    }

    public void initModel(GuiModel guiModel) {
        this.guiModel = guiModel;
    }
}
