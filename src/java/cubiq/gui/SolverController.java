package cubiq.gui;

import cubiq.models.GuiModel;
import cubiq.processing.MathUtils;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import java.util.*;

public class SolverController implements Observer {
     //TODO: Bilderbezeichnung noch ändern, aktuell 2R, ändern auf R2... usw.!!!!

    private final float ANIMATION_JUMP_SPEED = 1f;
    private GuiModel guiModel;
    private String imagePath;
    private int currentCycle, animationResetCycles, cycleCounter;
    private float startOffset, solvePaneOffset;
    private List<String> solution;
    private List<SolveIcon> solveIcons;
    private Timeline innerTimeline, outerTimeline, resetAnimationTimeline;

    @FXML
    private HBox solveIconPane, buttonPane;
    @FXML
    private VBox speedSliderPane;
    @FXML
    private Slider speedSlider;
    @FXML
    private Text typoSlow, typoFast;
    @FXML
    private ProgressBar speedProgressBar;
    @FXML
    private AnchorPane rendererPane;


    private void initializeSolverController() {
        guiModel.setRendererPaneSolver(rendererPane);
        solution = new ArrayList<>();
        solveIcons = new ArrayList<>();
        imagePath = "/assets/";

        buttonPane.getChildren().add(new ControlPane());

        solveIconPane.setVisible(true);
        speedSliderPane.setViewOrder(-1);
        speedSlider.setViewOrder(-2);
        typoSlow.setFont(guiModel.getKiona());
        typoSlow.setStyle("-fx-font-size: 12");
        typoSlow.setFontSmoothingType(FontSmoothingType.LCD);
        typoSlow.setFill(Color.web("#eaeaea"));
        typoFast.setFont(guiModel.getKiona());
        typoFast.setStyle("-fx-font-size: 12");
        typoFast.setFontSmoothingType(FontSmoothingType.LCD);
        typoFast.setFill(Color.web("#eaeaea"));

        speedSlider.valueProperty().addListener((ov, oldVl, newVl) -> {
            float value = newVl.floatValue();
            speedProgressBar.setProgress((value) * (1f / 3.2f));
            float rate = value + 0.8f;
            outerTimeline.setRate(value + 0.8f);
        });
    }

    private void initTimelines() {
        cycleCounter = 0;
        innerTimeline = new Timeline();
        innerTimeline.getKeyFrames().add(
                new KeyFrame(new Duration(1), e -> {
                    solvePaneOffset = MathUtils.easeInOut(currentCycle, startOffset, -149, Math.round(400 / ANIMATION_JUMP_SPEED));
                    currentCycle++;
                    solveIconPane.setPadding(new Insets(0, 0, 0, solvePaneOffset));
                })
        );
        innerTimeline.setCycleCount(Math.round(400 / ANIMATION_JUMP_SPEED));

        outerTimeline = new Timeline();
        outerTimeline.setRate(1);
        outerTimeline.getKeyFrames().add(
                new KeyFrame(new Duration(1800), e -> {
                    currentCycle = 0;
                    solvePaneOffset = Math.round(solvePaneOffset/ 149) * 149;
                    startOffset = solvePaneOffset;
                    guiModel.setActualSolveStep(solution.get(cycleCounter));
                    guiModel.callObservers("nextSolveStep");
                    cycleCounter++;
                    innerTimeline.play();
                })
        );
        outerTimeline.setCycleCount(solution.size()-1);

        resetAnimationTimeline = new Timeline();
        resetAnimationTimeline.getKeyFrames().add(
                new KeyFrame(new Duration(1), e -> {
                    solvePaneOffset = MathUtils.easeInOut(currentCycle, startOffset, startOffset*-1, animationResetCycles);
                    currentCycle++;
                    solveIconPane.setPadding(new Insets(0, 0, 0, solvePaneOffset));
                })
        );
    }

    private void loadSolutionIcons() {
        String solveString = guiModel.getSolveString();
        solveString = solveString.replace(" ", ",");
        int idx = 0;
        while (true) {
            int idxNew = solveString.indexOf(",", idx);
            if (idxNew != -1) {
                solution.add((solveString.substring(idx, idxNew)));
                idx = idxNew + 1;
            } else {
                break;
            }
        }
        // Load cube icons
        for (String s : solution) {
            SolveIcon solveIcon = new SolveIcon(s);
            solveIcons.add(solveIcon);
            Platform.runLater(() -> solveIconPane.getChildren().add(solveIcon));
        }
        initTimelines();
    }

    private void openSpeedSlider() {
    }

    class SolveIcon extends ImageView {
        public SolveIcon(String solveString) {
            Image image = new Image(getClass().getResourceAsStream(imagePath+"/solveIcons/"+solveString+".png"));
            this.setFitWidth(97);
            this.setFitHeight(144);
            this.setImage(image);
        }
    }

    class ControlPane extends AnchorPane {
        ImageView buttonLIcon, buttonMIcon, buttonRIcon;

        public ControlPane() {
            initializeRootPane();
            Polygon polygonLeft = generatePolygons(new Double[]{0.0, 0.0, 63.0, 63.0, 193.0, 63.0, 130.0, 0.0}, 0);
            Polygon polygonMiddle = generatePolygons(new Double[]{0.0, 0.0, 63.0, 63.0, 158.0, 63.0, 220.0, 0.0}, 1);
            Polygon polygonRight = generatePolygons(new Double[]{0.0, 0.0, -63.0, 63.0, 70.0, 63.0, 130.0, 0.0}, 2);
            loadImages();
            getChildren().addAll(polygonLeft, polygonMiddle, polygonRight);
            getChildren().addAll(buttonLIcon, buttonMIcon, buttonRIcon);
            AnchorPane.setLeftAnchor(buttonLIcon, 87d);
            AnchorPane.setTopAnchor(buttonLIcon,16d);
            AnchorPane.setLeftAnchor(buttonMIcon, 235d);
            AnchorPane.setTopAnchor(buttonMIcon, 16d);
            AnchorPane.setRightAnchor(buttonRIcon, 84d);
            AnchorPane.setTopAnchor(buttonRIcon, 16d);
            buttonLIcon.setMouseTransparent(true);
            buttonMIcon.setMouseTransparent(true);
            buttonRIcon.setMouseTransparent(true);
            buttonMIcon.setViewOrder(-2);
            polygonMiddle.setViewOrder(-1);
        }

        private void loadImages() {
            buttonLIcon = new ImageView();
            buttonLIcon.setFitHeight(30);
            buttonLIcon.setFitWidth(26);
            buttonMIcon = new ImageView();
            buttonMIcon.setFitHeight(30);
            buttonMIcon.setFitWidth(18);
            buttonRIcon = new ImageView();
            buttonRIcon.setFitHeight(30);
            buttonRIcon.setFitWidth(35);

            buttonLIcon.setImage(new Image(getClass().getResourceAsStream(imagePath + "restartButton.png")));
            buttonMIcon.setImage(new Image(getClass().getResourceAsStream(imagePath + "startButton.png")));
            buttonRIcon.setImage(new Image(getClass().getResourceAsStream(imagePath + "speedButton.png")));
        }

        private void initializeRootPane() {
            this.minWidth(USE_PREF_SIZE);
            this.minHeight(USE_PREF_SIZE);
            this.prefWidth(480);
            this.prefHeight(63);
            this.maxWidth(USE_PREF_SIZE);
            this.maxHeight(USE_PREF_SIZE);
        }

        private Polygon generatePolygons(Double[] polygonPoints, int id) {
            Polygon polygon = new Polygon();
            polygon.getPoints().addAll(polygonPoints);
            polygon.setFill(Color.web("#3f464f"));
            polygon.setStrokeWidth(0);
            polygon.setEffect(new DropShadow());

            setTopAnchor(polygon, 0.0);
            setBottomAnchor(polygon, 0.0);

            polygon.setOnMouseEntered(e -> polygon.setFill(Color.web("#e4e4e4")));
            polygon.setOnMouseExited(e -> polygon.setFill(Color.web("#3f464f")));
            switch (id) {
                case 0: // Restart iteration
                    polygon.setOnMousePressed(e -> {
                        outerTimeline.stop();
                        innerTimeline.stop();
                        buttonMIcon.setImage(new Image(getClass().getResourceAsStream(imagePath + "startButton.png")));
                        cycleCounter = 0;
                        currentCycle = 0;
                        startOffset = solvePaneOffset;
                        animationResetCycles = Math.round(Math.abs(solvePaneOffset / 3));
                        resetAnimationTimeline.setCycleCount(animationResetCycles);
                        resetAnimationTimeline.play();
                    });

                    setLeftAnchor(polygon, 0.0);
                    break;

                case 1: // Play/Pause
                    polygon.setOnMousePressed(e -> {
                        if (outerTimeline.getStatus() != Animation.Status.RUNNING && cycleCounter < solution.size() - 1) {
                            outerTimeline.play();
                            buttonMIcon.setImage(new Image(getClass().getResourceAsStream(imagePath + "pauseButton.png")));
                    } else {
                            outerTimeline.pause();
                            buttonMIcon.setImage(new Image(getClass().getResourceAsStream(imagePath + "startButton.png")));
                    }});
                    setLeftAnchor(polygon, 130.0);
                    break;

                case 2: // Set speed
                    polygon.setOnMousePressed(e -> openSpeedSlider());
                    setLeftAnchor(polygon, 288.0);
            }
            return polygon;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        switch ((String) arg) {
            case "initGuiElements":
                initializeSolverController();
                break;
            case "solutionFound":
                loadSolutionIcons();
                guiModel.callObservers("renderCubeSolver");
                break;
        }
    }

    public void initModel(GuiModel guiModel) {
        this.guiModel = guiModel;
    }
}
