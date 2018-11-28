package gameObjects;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import java.util.function.Consumer;

/**
 *
 *
 * @author Jason Zhou
 */
public interface GameObjectInstance {

    /**
     *
     * @return
     */
    ReadOnlyIntegerProperty getInstanceId();

    /**
     *
     * @param setFunc
     */
    void setInstanceId(Consumer<SimpleIntegerProperty> setFunc);

    /**
     *
     * @return
     */
    ReadOnlyStringProperty getClassName();

    /**
     *
     * @return
     */
    Consumer<GameObjectInstance> getReturnInstanceIdFunc();


    /**
     *
     * @param propertyName
     * @param defaultValue
     * @return
     */
    boolean addProperty(String propertyName, String defaultValue);

    /**
     *
     * @param propertyName
     * @return
     */
    boolean removeProperty(String propertyName);
}