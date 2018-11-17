package playing;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.function.Consumer;


public class EntityView implements Viewable, PropertyChangeListener {
    public static String REMOVE_KEY = "RemoveEntity";
    public static String CHANGE_IMAGE_KEY = "ChangeImage";
    public static String MOVE_KEY = "MoveEntity";

    private ImageView myImage;
    private Double posX;
    private Double posY;

    private PropertyChangeSupport mySupport;

    public EntityView(String imagePath, double xpos, double ypos){
        mySupport = new PropertyChangeSupport(this);
        myImage = new ImageView(imagePath);
        changeCoordinates(xpos, ypos);
    }

    @Override
    public void addListener(PropertyChangeListener listener) {
        mySupport.addPropertyChangeListener(listener);
    }

    @Override
    public ImageView getImageView() {
        return myImage;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals(CHANGE_IMAGE_KEY)){
            changeImage((String) evt.getNewValue());
        }
        else if(evt.getPropertyName().equals(MOVE_KEY)){
            changeCoordinates(((Pair<Double, Double>) evt.getNewValue()).getData1(), ((Pair<Double, Double>) evt.getNewValue()).getData2());
        }
    }

    @Override
    public void changeCoordinates(Double xPos, Double yPos) {
        posX = xPos;
        posY = yPos;
        myImage.setX(posX);
        myImage.setY(posY);
    }

    @Override
    public void changeImage(String path) {
        myImage.setImage(new Image(path));
    }

    @Override
    public void removeEntity(Scene currentScene) {
        Consumer<DisplayData> cons = (disp) -> {
            disp.removeEntity(this);
        };
        mySupport.firePropertyChange(REMOVE_KEY,this, cons);
    }
}
