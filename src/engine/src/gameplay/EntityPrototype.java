package gameplay;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.util.List;
import java.util.Map;

/**
 *  EntityPrototype is a builder for Entities;
 *  (I mean, it's a builder but one and only buildable component is the
 *   props, which is dealt on PropertyHolder)
 *
 *  So within groovy, one can do
 *
 *  GameData.createEntity("goblin", tileID); to use default hp
 *  GameData.createEntity("goblin", tileID).withProps("hp", 1); to override default hp
 *
 */
public class EntityPrototype extends PropertyHolder<EntityPrototype> {
    private String name;
    private List<String> myImagePaths;
    private String myImageSelector; // Groovy codee

    public Entity build(int id) { return new Entity(id, name, freshMap(), myImagePaths, myImageSelector); }

    public Entity build(int id, int tileID) { return new Entity(id, tileID, name, freshMap(), myImagePaths, myImageSelector); }

    public Map<String, Object> freshMap() {
        var serializer = new XStream(new DomDriver());
        return (Map<String, Object>) serializer.fromXML(serializer.toXML(props));
    }

    public String name() { return name; }
}
