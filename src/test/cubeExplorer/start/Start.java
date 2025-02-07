package cubeExplorer.start;

import cubeExplorer.gui.Controller;
import cubeExplorer.model.Model;
import cubeExplorer.processing.Renderer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.opencv.core.Core;

import java.util.ArrayList;
import java.util.List;

public class Start extends Application {

    @Override
    public void start(Stage stage) {
        // Load library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Init View
        Controller controller = new Controller();

        // Init Classes
        Renderer renderer = new Renderer();

        // Init Model
        Model model = new Model();

        controller.initModel(model);
        renderer.initModel(model);

        model.addObserver(controller);
        model.addObserver(renderer);

        model.setStage(stage);

        // Create a test color scheme-----------------------------------------------------------------------------------
        List<int[][]> colorScheme = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int[][] colorFace = new int[3][3];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    if (i == 3) colorFace[x][y] = i+1;
                    else if (i == 4) colorFace[x][y] = i-1;
                    else colorFace[x][y] = i;
                }
            }
            colorScheme.add(colorFace);
        }
        model.setColorScheme(colorScheme);

        // Init Scene---------------------------------------------------------------------------------------------------
        Scene scene = new Scene(controller);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResource("/assets/taskbarIcon.png").toExternalForm().replace("%20", " ")));
        stage.show();

        // Load Fonts
        Font kionaRegular = Font.loadFont(getClass().getResource("/fonts/Kiona-Regular.ttf").toExternalForm().replace("%20", " "), 30);
        model.setKionaRegular(kionaRegular);

        Font kionaItalic = Font.loadFont(getClass().getResource("/fonts/Kiona-Itallic.ttf").toExternalForm().replace("%20", " "), 17);
        model.setKionaItalic(kionaItalic);

        // Tell all observers, that the GUI has been initialized
        model.callObservers("guiInitialized");
        model.callObservers("startRenderer");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
