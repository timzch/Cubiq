package cubiq.gui;

import cubiq.models.GuiModel;
import javafx.beans.value.ChangeListener;
import javafx.scene.Cursor;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class ResizeFrame extends AnchorPane implements Observer {

    private GuiModel guiModel;
    private final Line[] lines;
    private final Point[] alignPoints;
    private double windowCursorPosX, windowCursorPosY;
    private double scenePosX, scenePosY;
    private double sceneWidth, sceneHeight;
    private double offsetX, offsetY;


    public ResizeFrame() {
        alignPoints = new Point[13];
        lines = new Line[12];
        this.setPickOnBounds(false);
    }

    private void addResizePointsUpdater() {
        ChangeListener sizeChangeListener = (ChangeListener<Double>) (observable, oldValue, newValue) -> alignResizeLines();
        guiModel.getStage().widthProperty().addListener(sizeChangeListener);
        guiModel.getStage().heightProperty().addListener(sizeChangeListener);
    }

    public void initResizeLines() {
        for (int i = 0; i < 13; i++) {
            alignPoints[i] = new Point();
        }
        for (int i = 0; i < 12; i++) {
            lines[i] = new Line();
            lines[i].setStrokeWidth(4);
            lines[i].setStroke(Color.TRANSPARENT);
            this.getChildren().add(lines[i]);
            switch (i) {
                case 0:
                    lines[i].setCursor(Cursor.N_RESIZE);
                    lines[i].setOnMousePressed(event -> {
                        windowCursorPosY = event.getScreenY();
                        scenePosY = guiModel.getStage().getY();
                        sceneHeight = guiModel.getStage().getHeight();
                    });
                    lines[i].setOnMouseDragged(event -> {
                        offsetY = event.getScreenY() - windowCursorPosY;
                        if (sceneHeight - offsetY > guiModel.getMinWindowSize()[1]) {
                            guiModel.getStage().setY(scenePosY + offsetY);
                            guiModel.getStage().setHeight(sceneHeight - offsetY);
                        }
                    });
                    continue;
                case 1:
                case 2:
                    lines[i].setCursor(Cursor.NE_RESIZE);
                    lines[i].setOnMousePressed(event -> {
                        windowCursorPosX = event.getScreenX();
                        windowCursorPosY = event.getScreenY();
                        scenePosY = guiModel.getStage().getY();
                        sceneWidth = guiModel.getStage().getWidth();
                        sceneHeight = guiModel.getStage().getHeight();
                    });
                    lines[i].setOnMouseDragged(event -> {
                        offsetX = event.getScreenX() - windowCursorPosX;
                        offsetY = event.getScreenY() - windowCursorPosY;
                        if (sceneWidth + offsetX > guiModel.getMinWindowSize()[0])
                            guiModel.getStage().setWidth(sceneWidth + offsetX);
                        if (sceneHeight - offsetY > guiModel.getMinWindowSize()[1]) {
                            guiModel.getStage().setHeight(sceneHeight - offsetY);
                            guiModel.getStage().setY(scenePosY + offsetY);
                        }
                    });
                    continue;
                case 3:
                    lines[i].setCursor(Cursor.E_RESIZE);
                    lines[i].setOnMousePressed(event -> {
                        windowCursorPosX = event.getScreenX();
                        sceneWidth = guiModel.getStage().getWidth();
                    });
                    lines[i].setOnMouseDragged(event -> {
                        offsetX = event.getScreenX() - windowCursorPosX;
                        if (sceneWidth + offsetX > guiModel.getMinWindowSize()[0])
                            guiModel.getStage().setWidth(sceneWidth + offsetX);
                    });
                    continue;
                case 4:
                case 5:
                    lines[i].setCursor(Cursor.SE_RESIZE);
                    lines[i].setOnMousePressed(event -> {
                        windowCursorPosX = event.getScreenX();
                        windowCursorPosY = event.getScreenY();
                        sceneWidth = guiModel.getStage().getWidth();
                        sceneHeight = guiModel.getStage().getHeight();
                    });
                    lines[i].setOnMouseDragged(event -> {
                        offsetX = event.getScreenX() - windowCursorPosX;
                        offsetY = event.getScreenY() - windowCursorPosY;
                        if (sceneWidth + offsetX > guiModel.getMinWindowSize()[0])
                            guiModel.getStage().setWidth(sceneWidth + offsetX);
                        if (sceneHeight + offsetY > guiModel.getMinWindowSize()[1])
                            guiModel.getStage().setHeight(sceneHeight + offsetY);
                    });
                    continue;
                case 6:
                    lines[i].setCursor(Cursor.S_RESIZE);
                    lines[i].setOnMousePressed(event -> {
                        windowCursorPosY = event.getScreenY();
                        sceneHeight = guiModel.getStage().getHeight();
                    });
                    lines[i].setOnMouseDragged(event -> {
                        offsetY = event.getScreenY() - windowCursorPosY;
                        if (sceneHeight + offsetY > guiModel.getMinWindowSize()[1])
                            guiModel.getStage().setHeight(sceneHeight + offsetY);
                    });
                    continue;
                case 7:
                case 8:
                    lines[i].setCursor(Cursor.SW_RESIZE);
                    lines[i].setOnMousePressed(event -> {
                        windowCursorPosX = event.getScreenX();
                        windowCursorPosY = event.getScreenY();
                        scenePosX = guiModel.getStage().getX();
                        sceneWidth = guiModel.getStage().getWidth();
                        sceneHeight = guiModel.getStage().getHeight();
                    });
                    lines[i].setOnMouseDragged(event -> {
                        offsetX = event.getScreenX() - windowCursorPosX;
                        offsetY = event.getScreenY() - windowCursorPosY;
                        if (sceneWidth - offsetX > guiModel.getMinWindowSize()[0]) {
                            guiModel.getStage().setX(scenePosX + offsetX);
                            guiModel.getStage().setWidth(sceneWidth - offsetX);
                        }
                        if (sceneHeight + offsetY > guiModel.getMinWindowSize()[1])
                            guiModel.getStage().setHeight(sceneHeight + offsetY);
                    });
                    continue;
                case 9:
                    lines[i].setCursor(Cursor.W_RESIZE);
                    lines[i].setOnMousePressed(event -> {
                        windowCursorPosX = event.getScreenX();
                        scenePosX = guiModel.getStage().getX();
                        sceneWidth = guiModel.getStage().getWidth();
                    });
                    lines[i].setOnMouseDragged(event -> {
                        offsetX = event.getScreenX() - windowCursorPosX;
                        if (sceneWidth - offsetX > guiModel.getMinWindowSize()[0]) {
                            guiModel.getStage().setX(scenePosX + offsetX);
                            guiModel.getStage().setWidth(sceneWidth - offsetX);
                        }
                    });
                    continue;
                case 10:
                case 11:
                    lines[i].setCursor(Cursor.NW_RESIZE);
                    lines[i].setOnMousePressed(event -> {
                        windowCursorPosX = event.getScreenX();
                        windowCursorPosY = event.getScreenY();
                        scenePosX = guiModel.getStage().getX();
                        scenePosY = guiModel.getStage().getY();
                        sceneWidth = guiModel.getStage().getWidth();
                        sceneHeight = guiModel.getStage().getHeight();
                    });
                    lines[i].setOnMouseDragged(event -> {
                        offsetX = event.getScreenX() - windowCursorPosX;
                        offsetY = event.getScreenY() - windowCursorPosY;
                        if (sceneWidth - offsetX > guiModel.getMinWindowSize()[0]) {
                            guiModel.getStage().setX(scenePosX + offsetX);
                            guiModel.getStage().setWidth(sceneWidth - offsetX);
                        }
                        if (sceneHeight - offsetY > guiModel.getMinWindowSize()[1]) {
                            guiModel.getStage().setY(scenePosY + offsetY);
                            guiModel.getStage().setHeight(sceneHeight - offsetY);
                        }
                    });
                    lines[i].setOnMouseReleased(event -> {
                        if (event.getScreenY() == 0 || event.getScreenY() == guiModel.getScreenHeight() - 1) {
                            guiModel.getStage().setHeight(guiModel.getScreenHeight() - 40);
                            guiModel.getStage().setY(0);
                        }
                    });
            }
        }
    }

    private void alignResizeLines() {
        alignPoints[0] = new Point(10, 2);
        alignPoints[1] = new Point((int) guiModel.getStage().getWidth() -10, 2);
        alignPoints[2] = new Point((int) guiModel.getStage().getWidth() - 2, 2);
        alignPoints[3] = new Point((int) guiModel.getStage().getWidth() - 2, 10);
        alignPoints[4] = new Point((int) guiModel.getStage().getWidth() - 2, (int) guiModel.getStage().getHeight() - 10);
        alignPoints[5] = new Point((int) guiModel.getStage().getWidth() - 2, (int) guiModel.getStage().getHeight() - 2);
        alignPoints[6] = new Point((int) guiModel.getStage().getWidth() - 10, (int) guiModel.getStage().getHeight() - 2);
        alignPoints[7] = new Point(10, (int) guiModel.getStage().getHeight() - 2);
        alignPoints[8] = new Point(2, (int) guiModel.getStage().getHeight() - 2);
        alignPoints[9] = new Point(2, (int) guiModel.getStage().getHeight() - 10);
        alignPoints[10] = new Point(2, 10);
        alignPoints[11] = new Point(2, 2);
        alignPoints[12] = new Point(10, 2);

        for (int i = 0; i < 12; i++) {
            lines[i].setStartX(alignPoints[i].getX());
            lines[i].setEndX(alignPoints[i + 1].getX());
            lines[i].setStartY(alignPoints[i].getY());
            lines[i].setEndY(alignPoints[i + 1].getY());
        }
    }

    private void maximizeHeight() {

    }

    @Override
    public void update(Observable o, Object arg) {
        switch ((String) arg) {
            case "initGuiElements":
                initResizeLines();
                addResizePointsUpdater();
                break;
            case "alignResizeLines":
                alignResizeLines();
                break;
            case "toggleDraggedFullScreen":
            case "toggleFullScreen":
                this.setDisable(!guiModel.getIsFullscreen());
                break;
        }
    }

    public void initModel(GuiModel guiModel) {
        this.guiModel = guiModel;
    }
}
