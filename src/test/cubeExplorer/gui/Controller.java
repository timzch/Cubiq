package cubeExplorer.gui;

import cubeExplorer.model.Model;
import javafx.scene.layout.AnchorPane;
import java.util.Observable;
import java.util.Observer;

public class Controller extends AnchorPane implements Observer{

    private Model model;

    public Controller() {
        this.setMinWidth(USE_PREF_SIZE);
        this.setMinHeight(USE_PREF_SIZE);
        this.setPrefWidth(1920);
        this.setPrefHeight(1080);
        this.setMaxWidth(USE_PREF_SIZE);
        this.setMaxHeight(USE_PREF_SIZE);
    }

    @Override
    public void update(Observable o, Object arg) {
        switch ((String)arg) {
            case "guiInitialized":
                model.setRendererPane(this);
                break;
        }
    }

    public void initModel(Model model) {
        this.model = model;
    }
}
