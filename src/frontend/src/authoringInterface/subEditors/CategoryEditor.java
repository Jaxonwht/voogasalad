package authoringInterface.subEditors;

import gameObjects.crud.GameObjectsCRUDInterface;
import gameObjects.category.CategoryClass;
import gameObjects.category.CategoryInstance;

/**
 * This class handles the addition and editing of category entries.
 *
 * @author Haotian Wang
 */
public class CategoryEditor extends AbstractGameObjectEditor<CategoryClass, CategoryInstance> {
    public CategoryEditor(GameObjectsCRUDInterface manager) {
        super(manager);
    }

    /**
     * This method brings up an editor that contains the data of an existing object that is already created.
     */
    @Override
    public void readGameObjectInstance() {

    }

    /**
     * Read the GameObjectClass represented by this editor.
     */
    @Override
    public void readGameObjectClass() {

    }
}
