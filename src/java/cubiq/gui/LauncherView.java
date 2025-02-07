package cubiq.gui;

import cubiq.models.GuiModel;
import javafx.geometry.Pos;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;

import java.util.Observable;
import java.util.Observer;

public class LauncherView extends AnchorPane implements Observer {

    GuiModel guiModel;

    private void initLauncher() {
        setMinWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setPrefWidth(800);
        setPrefHeight(450);
        setMaxWidth(USE_PREF_SIZE);
        setMaxHeight(USE_PREF_SIZE);
        setStyle("-fx-background-color: #0b1320");

        VBox contentPane = new VBox();
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setSpacing(60);
        contentPane.setPrefWidth(USE_COMPUTED_SIZE);
        contentPane.setPrefHeight(USE_COMPUTED_SIZE);
        setBottomAnchor(contentPane, 0d);
        setRightAnchor(contentPane, 0d);
        setTopAnchor(contentPane, 0d);
        setLeftAnchor(contentPane, 0d);
        getChildren().add(contentPane);

        Text title = new Text("cubiq");
        title.setFont(guiModel.getKiona());
        title.setStyle("-fx-font-size: 35");
        title.setFill(Color.web("#d5d5d5"));
        title.setFontSmoothingType(FontSmoothingType.GRAY);
        contentPane.getChildren().add(title);

        HBox menuButtonsPane = new HBox();
        menuButtonsPane.setAlignment(Pos.CENTER);
        setMinWidth(USE_COMPUTED_SIZE);
        menuButtonsPane.setMinHeight(USE_PREF_SIZE);
        menuButtonsPane.setPrefWidth(USE_COMPUTED_SIZE);
        menuButtonsPane.setPrefHeight(100);
        menuButtonsPane.setMaxWidth(USE_COMPUTED_SIZE);
        menuButtonsPane.setMaxHeight(USE_PREF_SIZE);
        contentPane.getChildren().add(menuButtonsPane);

        StackPane menuItem0 = new MenuItem("solver");
        StackPane menuItem1 = new MenuItem("explorer");
        StackPane menuItem2 = new MenuItem("timer");
        menuButtonsPane.getChildren().addAll(menuItem0, menuItem1, menuItem2);

        Text buildInfo = new Text("build 1.0");
        buildInfo.setFont(guiModel.getKiona());
        buildInfo.setStyle("-fx-font-size: 20");
        buildInfo.setFontSmoothingType(FontSmoothingType.GRAY);
        buildInfo.setFill(Color.web("#d5d5d5"));
        setBottomAnchor(buildInfo, 10d);
        setRightAnchor(buildInfo, 10d);
        getChildren().add(buildInfo);

        getChildren().add(new ExitButton());
    }

    private class MenuItem extends StackPane {
        private final String name;
        private final int width = 200;
        private final int height = 50;
        private final int skewValue = 10;

        /**
         * Generates a styled button with representative functions inside a stackpane
         * @param name The name of the button. It will appear at the button label and will help to choose the right
         *             button actions. The only valid values are: "use webcam", "load image" and "exit"
         */
        public MenuItem(String name) {
            this.name = name;

            initRootPane();
            Text title = generateText();
            Polygon polyBorder = generatePolyBorder(title);

            this.getChildren().addAll(polyBorder, title);
        }

        private void initRootPane() {
            setMinWidth(USE_PREF_SIZE);
            setMinHeight(USE_PREF_SIZE);
            setPrefWidth(width);
            setPrefHeight(height);
            setMaxWidth(USE_PREF_SIZE);
            setMaxHeight(USE_PREF_SIZE);
            setAlignment(Pos.CENTER);
        }

        private Polygon generatePolyBorder(Text title) {
            Polygon polyBorder = new Polygon(0, 0, skewValue, -height, width, -height, width - skewValue, 0);
            polyBorder.setFill(Color.TRANSPARENT);
            polyBorder.setStroke(Color.web("#64c4c0"));

            polyBorder.setOnMouseEntered(event -> {
                polyBorder.setFill(Color.web("#64c4c0"));
                title.setFill(Color.web("#0b1320"));
            });

            polyBorder.setOnMouseExited(event -> {
                polyBorder.setFill(Color.TRANSPARENT);
                title.setFill(Color.web("#64c4c0"));
            });

            switch (name) {
                case "solver":
                    polyBorder.setOnMouseClicked(e -> guiModel.callObservers("showScan"));
                    break;
                case "explorer":
                    polyBorder.setOnMouseClicked(e -> guiModel.callObservers("showExplorer"));
                    break;
                case "timer":
                    polyBorder.setOnMouseClicked(e -> guiModel.callObservers("showTimer"));
                    break;
            }

            return polyBorder;
        }

        private Text generateText() {
            Text title = new Text();
            title.setText(name);
            title.setFont(guiModel.getKionaItalic());
            title.setStyle("-fx-font-size: 17; -fx-font-style: italic");
            title.setFontSmoothingType(FontSmoothingType.GRAY);
            title.setFill(Color.web("#64c4c0"));
            title.setDisable(true);
            return title;
        }
    }

    private class ExitButton extends StackPane {

        public ExitButton() {
            setMinWidth(USE_PREF_SIZE);
            setMinHeight(USE_PREF_SIZE);
            setPrefWidth(30);
            setPrefHeight(23);
            setMaxWidth(USE_PREF_SIZE);
            setMaxHeight(USE_PREF_SIZE);

            AnchorPane.setTopAnchor(this, 0d);
            AnchorPane.setRightAnchor(this, 0d);

            Line line0 = new Line();
            line0.setLayoutX(0);
            line0.setLayoutY(0);
            line0.setStartX(0);
            line0.setEndX(7);
            line0.setStartY(0);
            line0.setEndY(7);
            line0.setStroke(Color.web("#d5d5d5"));

            Line line1 = new Line();
            line1.setLayoutX(0);
            line1.setLayoutY(0);
            line1.setStartX(0);
            line1.setEndX(7);
            line1.setStartY(0);
            line1.setEndY(-7);
            line1.setStroke(Color.web("#d5d5d5"));

            getChildren().addAll(line0, line1);

            setOnMousePressed(e -> guiModel.callObservers("shutdown"));
            setOnMouseEntered(e -> {
                line0.setStroke(Color.web("#d3453d"));
                line0.setEffect(new Glow(0.5));
                line1.setStroke(Color.web("#d3453d"));
                line1.setEffect(new Glow(0.5));
            });
            setOnMouseExited(e -> {
                line0.setStroke(Color.web("#d5d5d5"));
                line0.setEffect(null);
                line1.setStroke(Color.web("#d5d5d5"));
                line1.setEffect(null);
            });
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        switch ((String)arg) {
            case "initGuiElements":
                initLauncher();
                break;
        }
    }

    public void initModel(GuiModel guiModel) {
        this.guiModel = guiModel;
    }
}
