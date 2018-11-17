package entities;

import groovy.api.BlockGraph;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleTileClass implements TileClass {

    private String CONST_DEFAULTHEIGHT = "defaultHeight";
    private String CONST_DEFAULTWIDTH = "defaultWidth";
    private String CONST_ID = "id";
    private String CONST_SPRITECONTAINABLE = "spriteContainable";

    private ReadOnlyIntegerWrapper id;
    private SimpleIntegerProperty height;
    private SimpleIntegerProperty width;
    private SimpleBooleanProperty spriteContainable;
    private ObservableList<String> imagePathList;
    private ObservableMap<String, Integer> propertiesMap;
    private BlockGraph imageSelector;

    private SimpleTileClass() {
        id = new ReadOnlyIntegerWrapper(this, CONST_ID);
        height = new SimpleIntegerProperty(this, CONST_DEFAULTHEIGHT);
        width = new SimpleIntegerProperty(this, CONST_DEFAULTWIDTH);
        spriteContainable = new SimpleBooleanProperty(this, CONST_SPRITECONTAINABLE);
        imagePathList = FXCollections.observableArrayList();
        propertiesMap = FXCollections.observableHashMap();
    }

    SimpleTileClass(Consumer<SimpleIntegerProperty> setFunc) {
        this();

        setClassId(setFunc);
    }

    @Override
    public ReadOnlyIntegerProperty getClassId() {
        return id.getReadOnlyProperty();
    }

    @Override
    public void setClassId(Consumer<SimpleIntegerProperty> setFunc) {
        setFunc.accept(id);
    }

    @Override
    public Supplier<ReadOnlyIntegerProperty> returnClassId() {
        return this::getClassId;
    }

    @Override
    public ObservableMap<String, Integer> getPropertiesMap() {
        return propertiesMap;
    }

    @Override
    public boolean addProperty(String propertyName, int defaultValue) {
        if (!propertiesMap.containsKey(propertyName)) {
            propertiesMap.put(propertyName, defaultValue);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeProperty(String propertyName) {
        return propertiesMap.remove(propertyName) != null;
    }

    @Override
    public void setDefaultHeightWidth(int defaultHeight, int defaultWidth) {
        height.setValue(defaultHeight);
        width.setValue(defaultWidth);
    }

    @Override
    public SimpleIntegerProperty getDefaultHeight() {
        return height;
    }

    @Override
    public SimpleIntegerProperty getDefaultWidth() {
        return width;
    }


    @Override
    public ObservableList getImagePathList() {
        return imagePathList;
    }

    @Override
    public void addImagePath(String path) {
        imagePathList.add(path);
    }


    @Override
    public boolean removeImagePath(int index) {
        try {
            imagePathList.remove(index);
            return true;
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public void setImageSelector(BlockGraph blockCode) {
        imageSelector = blockCode;
    }

    @Override
    public BlockGraph getImageSelectorCode() {
        return imageSelector;
    }

    @Override
    public EntityInstance createInstance() {
        return null;
    }

    @Override
    public SimpleBooleanProperty isSpriteContainable() {
        return spriteContainable;
    }

    @Override
    public void setSpriteContainable(boolean contains) {
        spriteContainable.setValue(contains);
    }
}
