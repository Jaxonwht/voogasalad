package authoringInterface.subEditors;

import authoringInterface.subEditors.exception.IllegalGameObjectNamingException;
import authoringInterface.subEditors.exception.IllegalGeometryException;
import authoringUtils.exception.DuplicateGameObjectClassException;
import authoringUtils.exception.GameObjectClassNotFoundException;
import authoringUtils.exception.GameObjectInstanceNotFoundException;
import authoringUtils.exception.InvalidOperationException;
import gameObjects.crud.GameObjectsCRUDInterface;
import gameObjects.entity.EntityClass;
import gameObjects.entity.EntityInstance;
import gameObjects.player.PlayerClass;
import graphUI.groovy.GroovyPaneFactory.GroovyPane;
import grids.PointImpl;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.ErrorWindow;
import utils.exception.GridIndexOutOfBoundsException;
import utils.exception.PreviewUnavailableException;
import utils.exception.UnremovableNodeException;
import utils.imageManipulation.ImageManager;
import utils.imageManipulation.JavaFxOperation;
import utils.imageSelector.ImageSelectorController;
import utils.path.PathUtility;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;


/**
 * This is the editor for an "Entity" object that is opened when the user clicks on an existing entity or tries to add an entity to the game authoring library.
 *
 * @author Haotian Wang
 * @author Amy
 */
@SuppressWarnings("Duplicates")
public class EntityEditor extends AbstractGameObjectEditor<EntityClass, EntityInstance> {
    private static final double ICON_WIDTH = 50;
    private static final double ICON_HEIGHT = 50;
    private static final double REMOVE_OPACITY = 0.5;
    private static final int DEFAULT_HEIGHT = 1;
    private static final int DEFAULT_WIDTH = 1;
    private static final double IMAGE_PANEL_GAP = 10;
    private Label imageText;
    private Button chooseImage;
    private HBox imagePanel;
    private TextField widthInput;
    private TextField heightInput;
    private ObservableList<String> imagePaths;
    private Set<ImageView> toRemove;
    private Set<String> toRemovePath;
    private GridPane size;
    private TextField rowInput;
    private TextField colInput;
    private Label playerText;
    private ComboBox<PlayerClass> playerBox;
    private ImageSelectorController imageSelectorController;
    private Button imageSelectorButton;
    private GroovyPane imageSelectorPane;

    EntityEditor(GameObjectsCRUDInterface manager, ImageSelectorController imageSelectorController) {
        super(manager);
        toRemove = new HashSet<>();
        toRemovePath = new HashSet<>();
        nameLabel.setText("Your entity name:");

        size = new GridPane();
        size.setHgap(20);
        Label widthLabel = new Label("Width of entity");
        Label heightLabel = new Label("Height of entity");
        widthInput = new TextField(String.valueOf(DEFAULT_WIDTH));
        widthInput.setPromptText("width of entity");
        heightInput = new TextField(String.valueOf(DEFAULT_HEIGHT));
        heightInput.setPromptText("height of entity");
        size.addRow(0, widthLabel, widthInput);
        size.addRow(1, heightLabel, heightInput);

        playerText = new Label("Choose a player");
        var players = FXCollections.<PlayerClass>observableArrayList();
        manager.getPlayerClasses().forEach(players::add);
        playerBox = new ComboBox<>(players);
        imageText = new Label("Add an image to your entity");
        chooseImage = new Button("Choose image");
        chooseImage.setStyle("-fx-text-fill: white;"
                + "-fx-background-color: #343a40;");
        imagePanel = new HBox(IMAGE_PANEL_GAP);
        nameField.setPromptText("Your entity name");
        imagePaths = FXCollections.observableArrayList();
        imagePaths.addListener((ListChangeListener<String>) c -> presentImages());
        chooseImage.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(new Stage());
            if (file != null) {
                imagePaths.add(PathUtility.getRelativePath(file));
            }
        });
        this.imageSelectorController = imageSelectorController;
        imageSelectorButton = new Button("Image Selector");

        setupLayout();
        rootPane.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) {
                toRemovePath.forEach(path -> imagePaths.remove(path));
            }
        });
    }

    /**
     * This method sets up the confirm logic for adding new TreeItems.
     *
     * @throws IllegalGameObjectNamingException
     * @throws IllegalGeometryException
     * @throws PreviewUnavailableException
     * @throws DuplicateGameObjectClassException
     */
    @Override
    protected void confirmAddTreeItem() throws IllegalGameObjectNamingException, IllegalGeometryException, PreviewUnavailableException, DuplicateGameObjectClassException {
        EntityClass entityClass = gameObjectManager.createEntityClass(getValidClassName());
        int width = outputPositiveInteger(widthInput);
        int height = outputPositiveInteger(heightInput);
        assert entityClass != null;
        TreeItem<String> newItem = new TreeItem<>(entityClass.getClassName());
        entityClass.getImagePathList().addAll(imagePaths);
        entityClass.setHeight(height);
        entityClass.setWidth(width);
        ImageView icon = new ImageView(ImageManager.getPreview(entityClass));
        JavaFxOperation.setWidthAndHeight(icon, ICON_WIDTH, ICON_HEIGHT);
        newItem.setGraphic(icon);
        treeItem.getChildren().add(newItem);
        writeClassProperties();
    }

    /**
     * This method sets up the confirm logic of editing existing TreeItem.
     *
     * @throws IllegalGameObjectNamingException
     * @throws IllegalGeometryException
     * @throws InvalidOperationException
     * @throws PreviewUnavailableException
     * @throws DuplicateGameObjectClassException
     */
    @Override
    protected void confirmEditTreeItem() throws IllegalGameObjectNamingException, IllegalGeometryException, InvalidOperationException, PreviewUnavailableException, DuplicateGameObjectClassException {
        String newName = null;
        try {
            newName = getValidClassName();
        } catch (DuplicateGameObjectClassException e) {
            if (!nameField.getText().trim().equals(gameObjectClass.getClassName())) {
                throw e;
            }
        }
        if (newName == null) {
            newName = nameField.getText().trim();
        }
        int width = outputPositiveInteger(widthInput);
        int height = outputPositiveInteger(heightInput);
        try {
            ImageManager.removeClassImage(gameObjectClass);
        } catch (GameObjectClassNotFoundException ignored) {
        }
        gameObjectClass.getImagePathList().clear();
        gameObjectClass.getImagePathList().addAll(imagePaths);
        gameObjectClass.getPropertiesMap().clear();
        gameObjectClass.setWidth(width);
        gameObjectClass.setHeight(height);
        gameObjectManager.changeGameObjectClassName(gameObjectClass.getClassName(), newName);
        ImageView icon2 = new ImageView(ImageManager.getPreview(gameObjectClass));
        JavaFxOperation.setWidthAndHeight(icon2, ICON_WIDTH, ICON_HEIGHT);
        treeItem.setValue(newName);
        treeItem.setGraphic(icon2);
        writeClassProperties();
    }

    /**
     * This method sets up the confirm logic of editing existing Node.
     *
     * @throws IllegalGameObjectNamingException
     * @throws IllegalGeometryException
     * @throws PreviewUnavailableException
     * @throws GridIndexOutOfBoundsException
     * @throws UnremovableNodeException
     */
    @Override
    protected void confirmEditNode() throws IllegalGameObjectNamingException, IllegalGeometryException, PreviewUnavailableException, GridIndexOutOfBoundsException, UnremovableNodeException {
        gameObjectInstance.setInstanceName(getValidInstanceName());
        int width = outputPositiveInteger(widthInput);
        int height = outputPositiveInteger(heightInput);
        try {
            ImageManager.removeInstanceImage(gameObjectInstance);
        } catch (GameObjectInstanceNotFoundException ignored) {
        }
        gameObjectInstance.getImagePathList().clear();
        gameObjectInstance.getImagePathList().addAll(imagePaths);
        gameObjectInstance.getPropertiesMap().clear();
        gameObjectInstance.setWidth(width);
        gameObjectInstance.setHeight(height);
        var chosenPlayer = playerBox.getValue();
        if (chosenPlayer != null) chosenPlayer.addGameObjectInstances(gameObjectInstance);
        ((ImageView) nodeEdited).setImage(ImageManager.getPreview(gameObjectInstance));
        Tooltip.install(nodeEdited, new Tooltip(String.format("Width: %s\nHeight: %s\nSingle Click to toggle Deletion\nDouble Click or Right Click to edit\nInstance ID: %s\nClass Name: %s", width, height, gameObjectInstance.getInstanceId(), gameObjectInstance.getClassName())));
        int row = Integer.parseInt(rowInput.getText());
        int col = Integer.parseInt(colInput.getText());
        StackPane target = JavaFxOperation.getNodeFromGridPaneByIndices(((GridPane) JavaFxOperation.getGrandParent(nodeEdited)), row, col);
        JavaFxOperation.removeFromParent(nodeEdited);
        assert target != null;
        target.getChildren().add(nodeEdited);
        gameObjectInstance.setCoord(new PointImpl(col, row));
        writeInstanceProperties();
    }

    /**
     * Present the ImageViews contained in the imagePanel according to the ObservableList of ImagePaths.
     */
    private void presentImages() {
        imagePanel.getChildren().clear();
        imagePaths.forEach(path -> {
            ImageView preview = new ImageView(PathUtility.getAbsoluteURL(path));
            preview.setFitWidth(ICON_WIDTH);
            preview.setFitHeight(ICON_HEIGHT);
            imagePanel.getChildren().add(preview);
            preview.setOnMouseClicked(e -> {
                if (!toRemove.remove(preview)) {
                    System.out.println(path);
                    toRemove.add(preview);
                    toRemovePath.add(path);
                    preview.setOpacity(REMOVE_OPACITY);
                } else {
                    toRemovePath.remove(path);
                    preview.setOpacity(1);
                }
            });
        });
    }

    /**
     * This method brings up an editor that contains the data of an existing object that is already created.
     */
    @Override
    public void readGameObjectInstance() {
        readInstanceProperties();
        nameField.setText(gameObjectInstance.getInstanceName());
        imagePaths.addAll(gameObjectInstance.getImagePathList());
        widthInput.setText(String.valueOf(gameObjectInstance.getWidth()));
        heightInput.setText(String.valueOf(gameObjectInstance.getHeight()));
        gameObjectManager.getPlayerClasses().forEach(p -> {
            if (p.isOwnedByPlayer(gameObjectInstance)) {
                playerBox.getSelectionModel().select(p);
            }
        });
        Label xLabel = new Label("x, col index");
        Label yLabel = new Label("y, row index");
        colInput = new TextField(String.valueOf(gameObjectInstance.getCoord().getX()));
        rowInput = new TextField(String.valueOf(gameObjectInstance.getCoord().getY()));
        GridPane position = new GridPane();
        position.addRow(0, xLabel, colInput);
        position.addRow(1, yLabel, rowInput);
        position.setHgap(20);
        layout.addRow(6, playerText, playerBox);
        layout.addRow(7, position);
    }

    /**
     * Read the GameObjectClass represented by this editor.
     */
    @Override
    public void readGameObjectClass() {
        readClassProperties();
        nameField.setText(gameObjectClass.getClassName());
        imagePaths.addAll(gameObjectClass.getImagePathList());
        widthInput.setText(String.valueOf(gameObjectClass.getWidth()));
        heightInput.setText(String.valueOf(gameObjectClass.getHeight()));

        if (imageSelectorController != null)
            imageSelectorPane = imageSelectorController.groovyPaneOf(gameObjectClass);

        imageSelectorButton.setOnAction(e -> {
            if (imageSelectorPane == null) {
                ErrorWindow.display("Warning!", "You can only specify imageSelector on classes, not instances");
            } else imageSelectorPane.showWindow();
        });
    }

    private void setupLayout() {
        layout.addRow(0, size);
        layout.addRow(1, imageText, chooseImage);
        layout.addRow(2, imageSelectorButton);
        layout.addRow(3, imagePanel);
        layout.addRow(4, propLabel, addProperties);
        layout.addRow(5, listProp);
    }
}