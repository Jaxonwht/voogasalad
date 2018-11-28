package gameObjects.entity;

import gameObjects.gameObject.GameObjectInstance;
import javafx.beans.property.*;
import javafx.collections.ObservableMap;

import java.util.function.Consumer;

public class SimpleEntityInstance implements EntityInstance {
    private ReadOnlyStringWrapper className;
    private ReadOnlyIntegerWrapper instanceId;
    private SimpleIntegerProperty tileId;
    private ObservableMap<String, String> propertiesMap;
    private Consumer<GameObjectInstance> returnInstanceIdFunc;

    SimpleEntityInstance(String className, int tileId, ObservableMap<String, String> properties, Consumer<GameObjectInstance> returnInstanceIdFunc) {
        this.className = new ReadOnlyStringWrapper(className);
        this.tileId = new ReadOnlyIntegerWrapper();
        this.tileId.setValue(tileId);
        this.propertiesMap = properties;
        this.returnInstanceIdFunc = returnInstanceIdFunc;
        instanceId = new ReadOnlyIntegerWrapper();
    }

    @Override
    public ReadOnlyIntegerProperty getInstanceId() {
        return instanceId.getReadOnlyProperty();
    }

    @Override
    public void setInstanceId(Consumer<SimpleIntegerProperty> setFunc) {
        setFunc.accept(instanceId);
    }

    @Override
    public ReadOnlyStringProperty getClassName() { return className; }

    public void setClassName(String name) {
        className.set(name);
    }

    public Consumer<GameObjectInstance> getReturnInstanceIdFunc() {
        return returnInstanceIdFunc;
    }

    @Override
    public boolean addProperty(String propertyName, String defaultValue) {
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
    public SimpleIntegerProperty getTileID() { return tileId; }
}