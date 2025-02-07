package cubiq;

import cubiq.gui.*;
import cubiq.io.FileChooser;
import cubiq.models.GuiModel;
import cubiq.processing.BuildCube;
import cubiq.processing.ScanCube;
import cubiq.renderer.CubeRenderer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.opencv.core.Core;

public class Start extends Application {

    @Override
    public void start(Stage stage) {
        // Load library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Init Models--------------------------------------------------------------------------------------------------
        GuiModel guiModel = new GuiModel();
        guiModel.setStage(stage);

        // Init Gui-----------------------------------------------------------------------------------------------------
        GuiController guiController = new GuiController();
        guiController.initModel(guiModel);
        guiModel.addObserver(guiController);
        guiController.preloadGui();

        // Init ScanCube
        ScanCube scanCube = new ScanCube();
        scanCube.initModel(guiModel);
        guiModel.addObserver(scanCube);

        // Init FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.initModel(guiModel);
        guiModel.addObserver(fileChooser);

        // Init BuildCube
        BuildCube buildCube = new BuildCube();
        buildCube.initModel(guiModel);
        guiModel.addObserver(buildCube);

        // Init CubeExplorerRenderer
        CubeRenderer cubeRenderer = new CubeRenderer();
        cubeRenderer.initModel(guiModel);
        guiModel.addObserver(cubeRenderer);

        // Init ScreenInformation---------------------------------------------------------------------------------------
//        screenInformationModel.callObservers("initStageSize");

        // Load Fonts
        Font bender = Font.loadFont(getClass().getResource("/fonts/Bender-Light.ttf").toExternalForm().replace("%20", " "), 20);
        guiModel.setBender(bender);

        Font kiona = Font.loadFont(getClass().getResource("/fonts/Kiona-Regular.ttf").toExternalForm().replace("%20", " "), 18);
        guiModel.setKiona(kiona);

        Font kionaItalic = Font.loadFont(getClass().getResource("/fonts/Kiona-Itallic.ttf").toExternalForm().replace("%20", " "), 18);
        guiModel.setKionaItalic(kionaItalic);

        guiModel.callObservers("initGuiElements");
        guiModel.callObservers("showLauncher");

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/assets/taskbarIcon.png")));

        // Init Scene---------------------------------------------------------------------------------------------------
        Scene scene = new Scene(guiController, Color.TRANSPARENT);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();

        /*
        if (result.contains("Error"))
            switch (result.charAt(result.length() - 1)) {
                case '1':
                    result = "There are not exactly nine facelets of each color!";
                    break;
                case '2':
                    result = "Not all 12 edges exist exactly once!";
                    break;
                case '3':
                    result = "Flip error: One edge has to be flipped!";
                    break;
                case '4':
                    result = "Not all 8 corners exist exactly once!";
                    break;
                case '5':
                    result = "Twist error: One corner has to be twisted!";
                    break;
                case '6':
                    result = "Parity error: Two corners or two edges have to be exchanged!";
                    break;
                case '7':
                    result = "No solution exists for the given maximum move number!";
                    break;
                case '8':
                    result = "Timeout, no solution found within given maximum time!";
                    break;
            }
        System.out.println("RESULT: " + result);
         */
//        guiModel.callObservers("startLoadedImagesLoop");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
