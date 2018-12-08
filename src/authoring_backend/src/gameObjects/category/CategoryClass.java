package gameObjects.category;

import authoringUtils.exception.GameObjectTypeException;
import authoringUtils.exception.InvalidIdException;
import gameObjects.gameObject.GameObjectClass;
import gameObjects.gameObject.GameObjectType;
import javafx.beans.property.SimpleStringProperty;

/**
 * @author Haotian Wang
 */
public interface CategoryClass extends GameObjectClass {

    CategoryInstance createInstance() throws GameObjectTypeException, InvalidIdException;

    SimpleStringProperty getImagePath();

    void setImagePath(String newImagePath);

    @Override
    default GameObjectType getType() {
        return GameObjectType.CATEGORY;
    }
}
