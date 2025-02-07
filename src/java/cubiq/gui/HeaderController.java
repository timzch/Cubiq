package cubiq.gui;

import cubiq.models.GuiModel;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class HeaderController implements Observer {

    @FXML
    private StackPane sp_minimizePane, sp_minMaxPane, sp_exitPane;
    @FXML
    private Polygon pg_frameHeaderHitboxLeft, pg_frameHeaderHitboxCenter;
    @FXML
    private Text title;
    @FXML
    private AnchorPane ap_frameHeaderHitboxBackground;
    private double windowCursorPosX, windowCursorPosY;
    private double sceneOnWindowPosX, sceneOnWindowPosY;
    private boolean mousePressedInHeader = false;
    private GraphicsDevice[] graphicsDevices;
    private GuiModel guiModel;

    //TODO Multimonitor Unterstützung (MouseInfo.getPointerInfo().getDevice() abgleichen)
    //TODO Beim drag-fullscreen Windowposition nicht beim letzten draggen speichern (also y < 0), sondern beim mousePress


    private void initialize() {
        draggablePrimaryStage();
        redrawWindiowOnUnminimize();
        graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        title.setFont(guiModel.getKiona());
    }

    private void redrawWindiowOnUnminimize() {
        ChangeListener iconfieldChangeListener = (ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
            if (!guiModel.getStage().isIconified()) {
                guiModel.callObservers("toggleFullScreen");
                guiModel.callObservers("toggleFullScreen");
            }
        };
        guiModel.getStage().iconifiedProperty().addListener(iconfieldChangeListener);
    }

    private void draggablePrimaryStage() {

        EventHandler<MouseEvent> onMousePressed =
                event -> {
                    if (event.getSceneX() < guiModel.getStage().getWidth() - 105) {
                        mousePressedInHeader = true;
                        windowCursorPosX = event.getScreenX();
                        windowCursorPosY = event.getScreenY();
                        sceneOnWindowPosX = guiModel.getStage().getX();
                        sceneOnWindowPosY = guiModel.getStage().getY();
                    }
                };

        EventHandler<MouseEvent> onMouseDragged =
                event -> {
                    if (mousePressedInHeader) {
                        double offsetX = event.getScreenX() - windowCursorPosX;
                        double offsetY = event.getScreenY() - windowCursorPosY;
                        double newPosX = sceneOnWindowPosX + offsetX;
                        double newPosY = sceneOnWindowPosY + offsetY;
                        if (guiModel.getIsFullscreen()) {
                            guiModel.callObservers("toggleDraggedFullScreen"); //Wenn das Fenster im Vollbildmodus gedraggt wird, wird es verkleinert
                            sceneOnWindowPosX = guiModel.getStage().getX();
                        } else {
                            guiModel.getStage().setX(newPosX);
                            guiModel.getStage().setY(newPosY);
                        }
                    }
                };

        EventHandler<MouseEvent> onMouseReleased =
                event -> {
                    if (mousePressedInHeader) {
                        mousePressedInHeader = false;
                        if (MouseInfo.getPointerInfo().getLocation().y == 0) guiModel.callObservers("toggleFullScreen"); //Wenn das Fenster oben losgelassen wird, wird es in den Vollbildmodus gesetzt
                        else if (guiModel.getStage().getY() < 0) guiModel.getStage().setY(0); //Wenn das Fenster höher als 0 losgelassen wird, wird die Höhe auf 0 gesetzt
                        else if (guiModel.getStage().getY() + 30 > guiModel.getScreenHeight() - 40) guiModel.getStage().setY(guiModel.getScreenHeight() - 70); //Wenn das Fenster in der Taskbar losgelassen wird, wird es drüber gesetzt
                    }
                };

        EventHandler<MouseEvent> onMouseDoubleClicked =
                event -> {

                    if(event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY){
                        guiModel.callObservers("toggleFullScreen");
                    }

                };

        pg_frameHeaderHitboxLeft.setOnMousePressed(onMousePressed);
        pg_frameHeaderHitboxLeft.setOnMouseDragged(onMouseDragged);
        pg_frameHeaderHitboxLeft.setOnMouseReleased(onMouseReleased);
        pg_frameHeaderHitboxLeft.setOnMouseClicked(onMouseDoubleClicked);

        pg_frameHeaderHitboxCenter.setOnMousePressed(onMousePressed);
        pg_frameHeaderHitboxCenter.setOnMouseDragged(onMouseDragged);
        pg_frameHeaderHitboxCenter.setOnMouseReleased(onMouseReleased);
        pg_frameHeaderHitboxCenter.setOnMouseClicked(onMouseDoubleClicked);

        ap_frameHeaderHitboxBackground.setOnMousePressed(onMousePressed);
        ap_frameHeaderHitboxBackground.setOnMouseDragged(onMouseDragged);
        ap_frameHeaderHitboxBackground.setOnMouseReleased(onMouseReleased);
        ap_frameHeaderHitboxBackground.setOnMouseClicked(onMouseDoubleClicked);
    }

    @FXML
    private void mouseEnteredMinimize() {
        hoverFrameControls(sp_minimizePane, true);
    }

    @FXML
    private void mouseExitedMinimize() {
        hoverFrameControls(sp_minimizePane, false);
    }

    @FXML
    private void mouseEnteredMinMax() {
        hoverFrameControls(sp_minMaxPane, true);
    }

    @FXML
    private void mouseExitedMinMax() {
        hoverFrameControls(sp_minMaxPane, false);
    }

    @FXML
    private void mouseEnteredExit() {
        hoverFrameControls(sp_exitPane, true);
    }

    @FXML
    private void mouseExitedExit() {
        hoverFrameControls(sp_exitPane, false);
    }

    private void hoverFrameControls(StackPane stackPane, boolean hovered) {
        if (hovered) {
            for (int i = 0; i < stackPane.getChildren().size(); i++) {
                if (stackPane.equals(sp_exitPane))
                    stackPane.getChildren().get(i).setStyle("-fx-stroke: #cc2b2b");
                else
                    stackPane.getChildren().get(i).setStyle("-fx-stroke: #2bccbd");
            }
            stackPane.setEffect(new Glow(1));
        }
        else {
            for (int i = 0; i < stackPane.getChildren().size(); i++) {
                stackPane.getChildren().get(i).setStyle("-fx-stroke: #c9c9c9");
                stackPane.getChildren().get(i).setEffect(null);
            }
            stackPane.setEffect(null);
        }
    }

    @FXML
    private void minimize() {
        guiModel.getStage().setIconified(true);
    }
    @FXML
    private void minMax() {
        guiModel.callObservers("toggleFullScreen");
    }
    @FXML
    private void exit() {
        guiModel.callObservers("shutdown");
    }

    @Override
    public void update(Observable o, Object arg) {
        switch ((String) arg) {
            case "initGuiElements":
                initialize();
                break;
        }
    }

    public void initModel(GuiModel guiModel) {
        this.guiModel = guiModel;
    }
}
