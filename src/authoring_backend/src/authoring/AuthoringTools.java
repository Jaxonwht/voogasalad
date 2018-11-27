package authoring;


import gameObjects.GameObjectsCRUDInterface;
import gameObjects.SimpleGameObjectsCRUD;
import groovy.api.GroovyFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import phase.api.PhaseDB;

/**
 *  This class contains all the tools to author a game;
 */
public class AuthoringTools {
    private GroovyFactory factory;
    private GameObjectsCRUDInterface entityDB;
    private PhaseDB phaseDB;

    public AuthoringTools() {
        factory = new GroovyFactory();

        entityDB = new SimpleGameObjectsCRUD(5,5);

        phaseDB = new PhaseDB(factory);
    }

    public GroovyFactory factory() { return factory; }
    public GameObjectsCRUDInterface entityDB() { return entityDB; }
    public PhaseDB phaseDB() { return phaseDB; }

    // ... Not exactly, but something like this
    public String toEngineXML() { return Serializers.forEngine().toXML(this); }
    public String toAuthoringXML() { return null; }
}
