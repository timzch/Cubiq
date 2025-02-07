package cubiq.gui;

import cubiq.models.GuiModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.Observer;

public class GuiController extends AnchorPane implements Observer {

    private AnchorPane headerPane, solverPane, timerPane;
    private LauncherView launcherView;
    private ResizeFrame resizeFrame;
    private ScanView scanView;
    private GuiModel guiModel;
    private ExplorerView explorerView;

    public void preloadGui() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        guiModel.setScreenWidth(dimension.width);
        guiModel.setScreenHeight(dimension.height);

        // Launcher
        launcherView = new LauncherView();
        AnchorPane.setTopAnchor(launcherView, 0d);
        AnchorPane.setRightAnchor(launcherView, 0d);
        AnchorPane.setBottomAnchor(launcherView, 0d);
        AnchorPane.setLeftAnchor(launcherView, 0d);
        launcherView.initModel(guiModel);
        guiModel.addObserver(launcherView);

        // Resize frame
        resizeFrame = new ResizeFrame();
        resizeFrame.initModel(guiModel);
        guiModel.addObserver(resizeFrame);

        // Header
        FXMLLoader headerLoader = new FXMLLoader(getClass().getResource("/fxml/HeaderView.fxml"));
        try {
            headerPane = headerLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        headerPane.getStylesheets().add("css/HeaderStyle.css");
        AnchorPane.setTopAnchor(headerPane, 0d);
        AnchorPane.setLeftAnchor(headerPane, 0d);
        AnchorPane.setRightAnchor(headerPane, 0d);
        HeaderController headerController = headerLoader.getController();
        headerController.initModel(guiModel);
        guiModel.addObserver(headerController);

        // Scan
        scanView = new ScanView();
        AnchorPane.setTopAnchor(scanView, 23d);
        AnchorPane.setLeftAnchor(scanView, 0d);
        AnchorPane.setRightAnchor(scanView, 0d);
        AnchorPane.setBottomAnchor(scanView, 0d);
        scanView.initModel(guiModel);
        guiModel.addObserver(scanView);

        // Solver
        FXMLLoader solverLoader = new FXMLLoader(getClass().getResource("/fxml/SolverView.fxml"));
        try {
            solverPane = solverLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        headerPane.getStylesheets().add("css/SolverStyle.css");
        AnchorPane.setTopAnchor(headerPane, 0d);
        AnchorPane.setLeftAnchor(headerPane, 0d);
        AnchorPane.setRightAnchor(headerPane, 0d);
        SolverController solverController = solverLoader.getController();
        solverController.initModel(guiModel);
        guiModel.addObserver(solverController);

        // Explorer
        explorerView = new ExplorerView();
        AnchorPane.setTopAnchor(explorerView, 23d);
        AnchorPane.setLeftAnchor(explorerView, 0d);
        AnchorPane.setRightAnchor(explorerView, 0d);
        AnchorPane.setBottomAnchor(explorerView, 0d);
        explorerView.initModel(guiModel);
        guiModel.addObserver(explorerView);

        // Timer
        FXMLLoader timerLoader = new FXMLLoader(getClass().getResource("/fxml/TimerView.fxml"));
        try {
            timerPane = timerLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        timerPane.getStylesheets().add("css/TimerStyle.css");
        AnchorPane.setTopAnchor(timerPane, 23d);
        AnchorPane.setTopAnchor(timerPane, 0d);
        AnchorPane.setLeftAnchor(timerPane, 0d);
        AnchorPane.setRightAnchor(timerPane, 0d);
        TimerController timerController = timerLoader.getController();
        timerController.initModel(guiModel);
        guiModel.addObserver(timerController);
    }

    public void showLauncher() {
        // Set the launcher view on the empty root pane
        this.getChildren().clear();
        this.getChildren().add(launcherView);

        // Set the stage size to match the size of the launcher
        guiModel.getStage().setWidth(800);
        guiModel.getStage().setHeight(450);
    }

    private void showScan() {
        this.getChildren().clear();
        this.getChildren().add(scanView);
        this.getChildren().add(headerPane);
        this.getChildren().add(resizeFrame);

        guiModel.callObservers("toggleFullScreen");

        // Set the size the stage get set to when minimized
        guiModel.setSavedSceneWidth(guiModel.getScreenWidth() * 0.8);
        guiModel.setSavedSceneHeight(guiModel.getScreenHeight() * 0.8);
        guiModel.setSavedSceneX(guiModel.getScreenWidth() / 2 - guiModel.getSavedSceneWidth() / 2);
        guiModel.setSavedSceneY(guiModel.getScreenHeight() / 2 - guiModel.getSavedSceneHeight() / 2);

//        guiModel.callObservers("startLoadedImagesLoop");
        guiModel.callObservers("startScan");
    }

    private void showSolver() {
        this.getChildren().clear();
        this.getChildren().add(solverPane);
        this.getChildren().add(headerPane);
        this.getChildren().add(resizeFrame);

//        guiModel.callObservers("toggleFullScreen");

        // Set the size the stage get set to when minimized
        guiModel.setSavedSceneWidth(guiModel.getScreenWidth() * 0.8);
        guiModel.setSavedSceneHeight(guiModel.getScreenHeight() * 0.8);
        guiModel.setSavedSceneX(guiModel.getScreenWidth() / 2 - guiModel.getSavedSceneWidth() / 2);
        guiModel.setSavedSceneY(guiModel.getScreenHeight() / 2 - guiModel.getSavedSceneHeight() / 2);
    }

    private void showExplorer() {
        this.getChildren().clear();
        this.getChildren().add(explorerView);
        this.getChildren().add(headerPane);
        this.getChildren().add(resizeFrame);

        guiModel.callObservers("toggleFullScreen");
        guiModel.callObservers("renderCubeExplorer");

        // Set the size the stage get set to when minimized
        guiModel.setSavedSceneWidth(guiModel.getScreenWidth() * 0.8);
        guiModel.setSavedSceneHeight(guiModel.getScreenHeight() * 0.8);
        guiModel.setSavedSceneX(guiModel.getScreenWidth() / 2 - guiModel.getSavedSceneWidth() / 2);
        guiModel.setSavedSceneY(guiModel.getScreenHeight() / 2 - guiModel.getSavedSceneHeight() / 2);
    }

    private void showTimer() {
        this.getChildren().clear();
        this.getChildren().add(timerPane);
        this.getChildren().add(headerPane);
        this.getChildren().add(resizeFrame);

        guiModel.callObservers("toggleFullScreen");

        // Set the size the stage get set to when minimized
        guiModel.setSavedSceneWidth(guiModel.getScreenWidth() * 0.8);
        guiModel.setSavedSceneHeight(guiModel.getScreenHeight() * 0.8);
        guiModel.setSavedSceneX(guiModel.getScreenWidth() / 2 - guiModel.getSavedSceneWidth() / 2);
        guiModel.setSavedSceneY(guiModel.getScreenHeight() / 2 - guiModel.getSavedSceneHeight() / 2);
    }

    private void toggleFullScreen() {
        Stage stage = guiModel.getStage();
        if (guiModel.getIsFullscreen()) {
            stage.setX(guiModel.getSavedSceneX());
            stage.setY(guiModel.getSavedSceneY());
            stage.setWidth(guiModel.getSavedSceneWidth());
            stage.setHeight(guiModel.getSavedSceneHeight());
            guiModel.setIsFullscreen(false);
        } else {
            guiModel.setSavedSceneX(stage.getX());
            guiModel.setSavedSceneY(stage.getY());
            guiModel.setSavedSceneWidth(stage.getWidth());
            guiModel.setSavedSceneHeight(stage.getHeight());
            stage.setX(0);
            stage.setY(0);
            stage.setWidth(guiModel.getScreenWidth());
            stage.setHeight(guiModel.getScreenHeight() - guiModel.getTaskbarHeight());
            guiModel.setIsFullscreen(true);
        }
    }

    private void toggleDraggedFullScreen() {
        Stage stage = guiModel.getStage();
        if (guiModel.getIsFullscreen()) {
            double mousePosX = MouseInfo.getPointerInfo().getLocation().x;
            double screenWidthThird = guiModel.getScreenWidth() / 3;
            if (mousePosX <= screenWidthThird)
                stage.setX(1);
            else if (mousePosX > screenWidthThird & mousePosX < screenWidthThird * 2)
                stage.setX(guiModel.getScreenWidth() / 2 - guiModel.getSavedSceneWidth() / 2);
            else
                stage.setX(guiModel.getScreenWidth() - guiModel.getSavedSceneWidth());
            stage.setWidth(guiModel.getSavedSceneWidth());
            stage.setHeight(guiModel.getSavedSceneHeight());
            guiModel.setIsFullscreen(false);
        } else {
            guiModel.setSavedSceneWidth(stage.getWidth());
            guiModel.setSavedSceneHeight(stage.getHeight());
            stage.setX(0);
            stage.setY(0);
            stage.setWidth(guiModel.getScreenWidth());
            stage.setHeight(guiModel.getScreenHeight() - guiModel.getTaskbarHeight());
            guiModel.setIsFullscreen(true);
        }
    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        switch ((String) arg) {
            case "toggleFullScreen":
                toggleFullScreen();
                break;
            case "toggleDraggedFullScreen":
                toggleDraggedFullScreen();
                break;
            case "showLauncher":
                showLauncher();
                break;
            case "showScan":
                showScan();
                break;
            case "showSolver":
                showSolver();
                break;
            case "showExplorer":
                showExplorer();
                break;
            case "showTimer":
                showTimer();
                break;
        }
    }

    public void initModel(GuiModel guiModel) {
        this.guiModel = guiModel;
    }
}
