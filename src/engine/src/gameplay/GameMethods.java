package gameplay;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.lang.annotation.Repeatable;
import java.util.Comparator;
import java.util.List;

import static gameplay.GameData.*;

/**
 * Methods that are intended to be used by the authors via groovy blocks
 * Separated from GameData to use reflection
 * https://hackmd.io/kO3SRcCeQFyQJ_3VwEv1BQ?both#
 */
public class GameMethods {
    private static final int DEFAULT_PLAYER_ID = 0;

    /**
     * Grid
     */
    public static int gridWidth() {
        return GRID_WIDTH;
    }

    public static int gridHeight() {
        return GRID_HEIGHT;
    }

    /**
     * dot to function
     */
    public static int getId(GameObject object) {
        return object.getID();
    }

    public static String getName(GameObject object) {
        return object.getName();
    }

    public static double getX(GameObject object) {
        return object.getX();
    }

    public static double getY(GameObject object) {
        return object.getY();
    }

    public static void setProperty(PropertyHolder object, String key, Object value) {
        object.set(key, value);
    }

    public static Object getProperty(PropertyHolder object, String key) {
        return object.get(key);
    }

    /**
     * Entity
     */
    public static boolean isEntity(GameObject object) {
        return GameData.ENTITY_PROTOTYPES.keySet().contains(object.getName());
    }

    public static Entity getEntity(int entityID) {
        return ENTITIES.get(entityID);
    }

    public static Entity createEntity(String entityName, int x, int y, String ownerName) {
        var nextID = ENTITIES.keySet().stream().max(Comparator.comparingInt(a -> a)).orElse(0) + 1;
        var newEntity = ENTITY_PROTOTYPES.get(entityName).build(nextID, x, y);
        newEntity.adjustViewSize(ROOT.getWidth(), ROOT.getHeight());
        ENTITIES.put(nextID, newEntity);
    PLAYERS.get(ownerName).addEntity(nextID);
        newEntity.setLocation(x, y);
        ROOT.getChildren().add(newEntity.getImageView());
        return newEntity;
    }

    public static Entity createEntity(String entityName, Tile tile, String ownerName) {
        return createEntity(entityName, (int) Math.round(tile.getX()), (int) Math.round(tile.getY()), ownerName);
    }

    public static void removeEntity(Entity entity) {
        ROOT.getChildren().remove(entity.getImageView());
        ENTITIES.remove(entity.getID());
        PLAYERS.values().forEach(p -> p.getMyEntities().remove(entity.getID()));
    }

    public static void moveEntity(Entity entity, double x, double y) {
        entity.setLocation(x, y);
    }

    public static void moveEntity(Entity entity, Tile tile) {
        moveEntity(entity, tile.getX(), tile.getY());
    }

    public static Entity getEntityOver(Tile tile) {
        for(var e : ENTITIES.values()) {
            boolean verdictX =
                (tile.getX() <= e.getX() && e.getX() < tile.getX() + tile.getWidth()) ||
                    (e.getX() <= tile.getX() && tile.getX() < e.getX() + e.getWidth());
            boolean verdictY =
                (tile.getY() <= e.getY() && e.getY() < tile.getY() + tile.getHeight()) ||
                    (e.getY() <= tile.getY() && tile.getY() < e.getY() + e.getHeight());
            if(verdictX && verdictY) return e;
        } return null;
    }
    /**
     * Tile
     */
    public static boolean isTile(GameObject object) {
        return !ENTITY_PROTOTYPES.keySet().contains(object.getName());
    }

    public static Tile getTile(int tileID) {
        return TILES.get(tileID);
    }

    public static boolean hasNoIntersectingEntities(int tileID) {
        return hasNoIntersectingEntities(TILES.get(tileID));
    }

    public static boolean hasNoIntersectingEntities(Tile tile) {
        return ENTITIES.values().stream().noneMatch(e -> {
            boolean verdictX =
                    (tile.getX() <= e.getX() && e.getX() < tile.getX() + tile.getWidth()) ||
                            (e.getX() <= tile.getX() && tile.getX() < e.getX() + e.getWidth());
            boolean verdictY =
                    (tile.getY() <= e.getY() && e.getY() < tile.getY() + tile.getHeight()) ||
                            (e.getY() <= tile.getY() && tile.getY() < e.getY() + e.getHeight());
            return verdictX && verdictY;
        });
    }

    public static boolean hasNoEntityAt(int x, int y) {
        return ENTITIES.values().stream().noneMatch(e -> {
            boolean verdictX = (e.getX() <= x && x < e.getX() + e.getWidth());
            boolean verdictY = (e.getY() <= y && y < e.getY() + e.getHeight());
            return verdictX && verdictY;
        });
    }

    public static Tile getTileAt(double x, double y) {
        return TILES.values().stream().filter(t -> t.getX() == x && t.getY() == y).findFirst().get();
    }

    public static Tile getTileUnder(Entity entity) {
        return getTileAt(entity.getX(), entity.getY());
    }

    /**
     * Player/Turn
     */
    public static Player getCurrentPlayer() {
        return PLAYERS.get(TURN.getCurrentPlayerName());
    }

    public static void setCurrentPlayer(String playerName) {
        TURN.setCurrentPlayer(playerName);
    }

    public static Double getCurrentPlayerStats(String stat) {
        return getCurrentPlayer().getValue(stat);
    }

    public static String getCurrentPlayerName() {
        return TURN.getCurrentPlayerName();
    }

    public static String getNextPlayerName() {
        return TURN.nextPlayerName();
    }

    public static Player getPlayer(String playername) { return PLAYERS.get(playername); }

    public static String toNextPlayer() {
        return TURN.toNextPlayer();
    }

    public static void setPlayerOrder(List<String> newOrder) {
        TURN.setPlayerOrder(newOrder);
    }

    public static boolean hasNoEntities(String playerName) {
        return PLAYERS.get(playerName).getMyEntities().size() == 0;
    }

    public static boolean isEntityOf(String playerName, Entity entity) {
        return PLAYERS.get(playerName).getMyEntities().contains(entity.getID());
    }

    public static void endGame(String endingMessage) {
        TURN.endGame(endingMessage);
    }

    public static int numberOfInstances(String entityName) {
        return (int) ENTITIES.values().stream().filter(e -> e.getName().equals(entityName)).count();
    }

    /**
     * Phase
     */
    public static void $goto(String phaseNodeName) {
        getNode(phaseNodeName).execute();
    }

    /**
     * Meta
     */
    public static double distance(GameObject a, GameObject b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        System.out.println("Distance: " + Math.sqrt(dx * dx + dy * dy));
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static void $print(Object obj) { System.out.println("[DEBUG]: "+ obj); }

    public static void $return(Object retVal) {
        GameData.shell().setVariable("$return", retVal);
    }

    public static GameObject $this() {
        return (GameObject) GameData.shell().getVariable("$this");
    }

    public static boolean isNull(Object obj) { return obj == null; }

    public static boolean not(boolean bool) { return !bool; }

    public static void updateViews() {
        ENTITIES.values().forEach(Entity::updateView);
        TILES.values().forEach(Tile::updateView);
    }

    public static void DO_LOT_OF_THINGS() { }

    public static Tile getEmptyTileBelow(Tile t) {
        for (int i = gridHeight()-1; i >= 0; i--) {
            var tile = getTileAt(t.getX(), i);
            if(hasNoIntersectingEntities(tile)) return tile;
        } return null;
    }

    public static boolean check4(String entityName) {
        var entities = new Entity[gridHeight()][gridWidth()];
        for (int i = 0 ; i < gridHeight() ; i ++) {
            for(int j = 0 ; j < gridWidth() ; j ++) {
                entities[i][j] = getEntityOver(getTileAt(j, i));
            }
        }

        // horizontal check
        for (int i = 0 ; i < gridHeight() ; i ++) {
            int counter = 0;
            for (int j = 0 ; j < gridWidth() ; j ++) {
                counter = entities[i][j] != null && entities[i][j].getName().equals(entityName) ? counter + 1 : 0;
                if(counter == 4) return true;
            }
        }
        // vertical check
        for (int j = 0 ; j < gridWidth() ; j ++) {
            int counter = 0;
            for (int i = 0 ; i < gridHeight() ; i ++) {
                counter = entities[i][j] != null && entities[i][j].getName().equals(entityName) ? counter + 1 : 0;
                if(counter == 4) return true;
            }
        }
        // diagonal check1
        for (int k = -gridHeight() ; k < gridWidth() ; k ++) {
            int counter = 0;
            for(int i = 0 ; i < gridWidth() ; i ++) {
                if(0 <= i-k && i-k < gridHeight()) {
                    counter = entities[i - k][i] != null && entities[i - k][i].getName().equals(entityName) ? counter + 1 : 0;
                    if (counter == 4) return true;
                }
            }
        }

        // diagonal check2
        for (int k = 0 ; k < gridWidth()+gridHeight()-1 ; k ++) {
            int counter = 0;
            for(int i = 0 ; i < gridWidth() ; i ++) {
                if(0 <= k-i && k-i < gridHeight()) {
                    counter = entities[k-i][i] != null && entities[k-i][i].getName().equals(entityName) ? counter + 1 : 0;
                    if (counter == 4) return true;
                }
            }
        }

        return false;
    }
}
