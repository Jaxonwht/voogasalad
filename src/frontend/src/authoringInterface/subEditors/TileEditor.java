package authoringInterface.subEditors;

import authoringUtils.exception.DuplicateGameObjectClassException;
import authoringUtils.exception.GameObjectClassNotFoundException;
import authoringUtils.exception.InvalidOperationException;
import gameObjects.crud.GameObjectsCRUDInterface;
import gameObjects.tile.TileClass;
import gameObjects.tile.TileInstance;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.ErrorWindow;
import utils.exception.GameObjectInstanceNotFoundException;
import utils.exception.PreviewUnavailableException;
import utils.imageManipulation.JavaFxOperation;
import utils.imageManipulation.ImageManager;

import java.io.File;

/**
 * Editor to change the Tile settings. Need to work on it. Low priority
 *
 * @author Haotian Wang, jl729
 */

public class TileEditor extends AbstractGameObjectEditor<TileClass, TileInstance> {
    private TextField widthText = new TextField();
    private TextField heightText = new TextField();
    private GridPane geometry = new GridPane();
    private double DEFAULT_HEIGHT = 50;
    private double DEFAULT_WIDTH = 50;
    private double width = DEFAULT_HEIGHT;
    private double height = DEFAULT_WIDTH;
    private HBox imagePanel = new HBox();
    private Label imageLabel = new Label("Add an image to your tile class");
    private static final double ICON_WIDTH = 50;
    private static final double ICON_HEIGHT = 50;
    private Button chooseButton = new Button("Choose image");


    TileEditor(GameObjectsCRUDInterface manager) {
        super(manager);
        Label widthLabel = new Label("Width");
        Label heightLabel = new Label("Height");
        nameLabel.setText("Your tile name");
        widthText.setPromptText("Width");
        widthText.setText(String.valueOf(DEFAULT_WIDTH));
        heightText.setPromptText("Height");
        heightText.setText(String.valueOf(DEFAULT_HEIGHT));
        nameField.setPromptText("Tile name");
        geometry.setHgap(70);
        geometry.addRow(0, widthLabel, widthText);
        geometry.addRow(1, heightLabel, heightText);
        chooseButton.setStyle("-fx-text-fill: white;"
                            + "-fx-background-color: #343a40;");
        chooseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                String imagePath = file.toURI().toString();
                imagePaths.add(imagePath);
            }
        });

        rootPane.getChildren().addAll(geometry, chooseButton, imageLabel, imagePanel);

        confirm.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty()) {
                new ErrorWindow("Empty name", "You must give your tile a non-empty name").showAndWait();
            } else if (widthText.getText().trim().isEmpty()) {
                new ErrorWindow("Empty width", "You must specify a width for this tile class").showAndWait();
            } else if (heightText.getText().trim().isEmpty()) {
                new ErrorWindow("Empty height", "You must specify a height for this tile class").showAndWait();
            } else {
                try {
                    width = Double.parseDouble(widthText.getText());
                } catch (NumberFormatException e1) {
                    new ErrorWindow("Incorrect width", "The input width is in an unsupported format").showAndWait();
                    return;
                }
                try {
                    height = Double.parseDouble(heightText.getText());
                } catch (NumberFormatException e1) {
                    new ErrorWindow("Incorrect height", "The input height is in an unsupported format").showAndWait();
                    return;
                }
                ((Stage) rootPane.getScene().getWindow()).close();
                switch (editingMode) {
                    case ADD_TREEITEM:
                        try {
                            gameObjectManager.createTileClass(nameField.getText().trim());
                        } catch (DuplicateGameObjectClassException e1) {
                            // TODO
                            e1.printStackTrace();
                        }
                        TileClass tileClass = null;
                        try {
                            tileClass = gameObjectManager.getTileClass(nameField.getText().trim());
                        } catch (GameObjectClassNotFoundException e1) {
                            // TODO
                            e1.printStackTrace();
                        }
                        TreeItem<String> newItem = new TreeItem<>(tileClass.getClassName().getValue());
                        tileClass.getImagePathList().addAll(imagePaths);
                        ImageView icon = null;
                        try {
                            icon = new ImageView(ImageManager.getPreview(tileClass));
                        } catch (PreviewUnavailableException e1) {
                            // TODO: proper error handling
                            e1.printStackTrace();
                        }
                        JavaFxOperation.setWidthAndHeight(icon, ICON_WIDTH, ICON_HEIGHT);
                        newItem.setGraphic(icon);
                        treeItem.getChildren().add(newItem);
                        break;
                    case NONE:
                        return;
                    case EDIT_NODE:
                        try { ImageManager.removeInstanceImage(gameObjectInstance); } catch (GameObjectInstanceNotFoundException e1) {}
                        gameObjectInstance.setInstanceName(nameField.getText());
                        gameObjectInstance.getImagePathList().clear();
                        gameObjectInstance.getImagePathList().addAll(imagePaths);
                        try {
                            ((ImageView) nodeEdited).setImage(ImageManager.getPreview(gameObjectInstance));
                        } catch (PreviewUnavailableException e1) {
                            // TODO: proper error handling
                            e1.printStackTrace();
                        }
                        break;
                    case EDIT_TREEITEM:
                        try { ImageManager.removeClassImage(gameObjectClass); } catch (utils.exception.GameObjectClassNotFoundException e1) {}
                        gameObjectClass.getImagePathList().clear();
                        gameObjectClass.getImagePathList().addAll(imagePaths);
                        try {
                            gameObjectManager.changeGameObjectClassName(gameObjectClass.getClassName().getValue(), nameField.getText());
                        } catch (InvalidOperationException e1) {
                            // TODO
                            e1.printStackTrace();
                        }
                        ImageView icon2 = null;
                        try {
                            icon2 = new ImageView(ImageManager.getPreview(gameObjectClass));
                        } catch (PreviewUnavailableException e1) {
                            // TODO: proper error handling
                            e1.printStackTrace();
                        }
                        JavaFxOperation.setWidthAndHeight(icon2, ICON_WIDTH, ICON_HEIGHT);
                        treeItem.setValue(nameField.getText());
                        treeItem.setGraphic(icon2);
                        break;
                }
            }
        });
        setupLayout();
    }

    /**
     * This method brings up an editor that contains the data of an existing object that is already created.
     */
    @Override
    public void readGameObjectInstance() {
        nameField.setText(gameObjectInstance.getClassName().getValue());
        // TODO: REmove this disgusting shite
        try {
            imagePaths.addAll(gameObjectManager.getTileClass(gameObjectInstance.getClassName().getValue()).getImagePathList());
        } catch (GameObjectClassNotFoundException e) {
            // TODO
            e.printStackTrace();
        }
    }

    /**
     * Read the GameObjectClass represented by this editor.
     */
    @Override
    public void readGameObjectClass() {
        nameField.setText(gameObjectClass.getClassName().getValue());
        // TODO: REmove this disgusting shite
        imagePaths.addAll(gameObjectClass.getImagePathList());
    }

    private void setupLayout() {
        geometry.setLayoutX(50);
        geometry.setLayoutY(100);
        imagePanel.setLayoutX(50);
        imagePanel.setLayoutY(350);
        imageLabel.setLayoutX(50);
        imageLabel.setLayoutY(300);
        chooseButton.setLayoutX(350);
        chooseButton.setLayoutY(300);
    }
}
