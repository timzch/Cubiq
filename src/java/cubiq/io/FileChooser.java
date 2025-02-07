package cubiq.io;

import cubiq.models.GuiModel;
import javafx.scene.control.Alert;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class FileChooser implements Observer {

    private GuiModel model;
    private final javafx.stage.FileChooser fileChooser;


    public FileChooser() {
        fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Select an image");

        // Set the standard location, where the file chooser starts
        fileChooser.setInitialDirectory(new File("src/resources/cubeImages"));

        // Set the extensions that you can select with the file chooser
        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new javafx.stage.FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new javafx.stage.FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    /**
     * Opens the file chooser, reads the selected image and loads it into the model
     */
    private void loadImage() {
        String rootDirectory = fileChooser.showOpenDialog(model.getStage()).getParent();
        if (rootDirectory.equals("")) return;

        Mat[] cubeImages = new Mat[6];
        cubeImages[0] = Imgcodecs.imread(rootDirectory + "/white.jpg");
        cubeImages[1] = Imgcodecs.imread(rootDirectory + "/yellow.jpg");
        cubeImages[2] = Imgcodecs.imread(rootDirectory + "/red.jpg");
        cubeImages[3] = Imgcodecs.imread(rootDirectory + "/orange.jpg");
        cubeImages[4] = Imgcodecs.imread(rootDirectory + "/green.jpg");
        cubeImages[5] = Imgcodecs.imread(rootDirectory + "/blue.jpg");

        for (Mat cubeImage : cubeImages) {
            if (cubeImage.empty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Loading File");
                alert.setHeaderText("Could not load the images");
                alert.setContentText("Choose another directory or add images");
                alert.showAndWait();
                loadImage();
                return;
            }
            cubeImage.convertTo(cubeImage, CvType.CV_8UC3);
        }
        model.setLoadedImages(cubeImages);
    }

    @Override
    public void update(Observable o, Object arg) {
        switch ((String)arg) {
            case "loadImage":
                loadImage();
                break;
        }
    }

    public void initModel(GuiModel model) {
        this.model = model;
    }
}
