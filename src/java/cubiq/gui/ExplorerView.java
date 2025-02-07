package cubiq.gui;

import cubiq.models.GuiModel;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.util.Observable;
import java.util.Observer;

public class ExplorerView extends StackPane implements Observer {

    private GuiModel guiModel;
    private AnchorPane rendererPane;

    private void init() {
        this.setPrefWidth(1920);
        this.setPrefHeight(1080);
        rendererPane = new AnchorPane();
        this.getChildren().add(rendererPane);
        this.setStyle("-fx-background-color: #191b1d");
        guiModel.setRendererPaneExplorer(rendererPane);
        rendererPane.setLayoutX((1920f/2f) - (900f/2f));
        rendererPane.setLayoutY((1080f/2f) - (900f/2f));
    }

    @Override
    public void update(Observable o, Object arg) {
        switch ((String)arg) {
            case "initGuiElements":
                init();
                break;
        }
    }

    public void initModel(GuiModel guiModel) {
        this.guiModel = guiModel;
    }
}
