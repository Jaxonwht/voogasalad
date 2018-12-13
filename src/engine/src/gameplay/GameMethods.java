package gameplay;

import javafx.util.Pair;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.lang.annotation.Repeatable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        return TILES.values().stream().filter(t -> t.getX() == x && t.getY() == y).findFirst().orElse(null);
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
        System.out.println("next play: " + TURN.nextPlayerName());
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
        var count = (int) ENTITIES.values().stream().filter(e -> e.getName().equals(entityName)).count();
        System.out.println("Instance Count: " + count);
        return count;
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

    private static Map<Pair<Integer, Integer>, Integer> createMap(){
        Map<Pair<Integer, Integer>, Integer> map = new HashMap<>();
        ENTITIES.entrySet().forEach(a -> {
            var id = a.getKey();
            var entity = a.getValue();
            var x = roundDouble(entity.getX());
            var y = roundDouble(entity.getY());
            map.put(new Pair<>(x, y), id);
        });
        return map;
    }

    private static Map<Pair<Integer, Integer>, String> intMap(Map<Pair<Integer, Integer>, Integer> entityMap){
        Map<Pair<Integer, Integer>, String> map = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!hasNoEntityAt(i, j)){
                    map.put(new Pair<>(i, j), ENTITIES.get(entityMap.get(new Pair<>(i,j))).getName());
                }
            }
        }
        return map;
    }

    public static void checkNeighbor(GameObject a, String currentPlayer){
        var entityMap = createMap();
        System.out.println("entityMap: " + entityMap);
        var numMap = intMap(entityMap);
        System.out.println("numMap: " + numMap);
        var x = (int) Math.round(a.getX());
        var y = (int) Math.round(a.getY());
        var myId = a.getID();
        var type = a.getName();
        System.out.println("ID: " + myId + "current x: " + x + " current y: " + y);
        numMap.keySet().forEach(e -> {
            var xx = e.getKey();
            var yy = e.getValue();
            if (numMap.get(e).equals(type)){
                if (x == xx && y != yy) {
                    System.out.println("current xx: " + xx + " current yy: " + yy);
                    var minY = Math.min(y, yy);
                    var maxY = Math.max(y, yy);
                    boolean flanked = true;
                    for (int i = minY + 1; i < maxY; i++) {
                        if (hasNoEntityAt(x, i)) flanked = false;
                    }
                    if (flanked) {
                        System.out.println("flank detected along Y");
                        for (int j = minY + 1; j < maxY; j++) {
                            System.out.println("trying to create an entity at: " + x + ", " + j);
                            System.out.println("entity map: " + ENTITIES);
                            var entityID = entityMap.get(new Pair<>(x,j));
                            removeEntity(ENTITIES.get(entityID));
                            if (type.equals("r_black")) replaceEntity("r_black", x, j, currentPlayer, entityID);
                            else replaceEntity("r_white", x, j, currentPlayer, entityID);
                        }
                    }
                } else if (y == yy && x != xx) {
                    System.out.println("current xx: " + xx + " current yy: " + yy);
                    var minX = Math.min(x, xx);
                    var maxX = Math.max(x, xx);
                    boolean flanked = true;
                    for (int i = minX + 1; i < maxX; i++) {
                        if (hasNoEntityAt(i, y)) flanked = false;
                    }
                    if (flanked) {
                        System.out.println("flanked detected along X");
                        for (int j = minX + 1; j < maxX; j++) {
                            var id = entityMap.get(new Pair<>(j,y));
                            removeEntity(ENTITIES.get(id));
                            System.out.println("trying to create an entity at: " + j + ", " + y );
                            if (type.equals("r_black")) replaceEntity("r_black", j, y, currentPlayer, id);
                            else replaceEntity("r_white", j, y, currentPlayer, id);
                        }
                    }
                }
            }
        });
        System.out.println("Board Size + " + ENTITIES.keySet().size());
    }

    public static int roundDouble(double i){
        return (int) Math.round(i);
    }

    public static boolean stringComp(String a, String b){
        return a.equals(b);
    }

    private static Entity replaceEntity(String entityName, int x, int y, String ownerName, int nextID) {
        var newEntity = ENTITY_PROTOTYPES.get(entityName).build(nextID, x, y);
        newEntity.adjustViewSize(ROOT.getWidth(), ROOT.getHeight());
        ENTITIES.put(nextID, newEntity);
        PLAYERS.get(ownerName).addEntity(nextID);
        newEntity.setLocation(x, y);
        ROOT.getChildren().add(newEntity.getImageView());
        return newEntity;
    }

    public static int boardSize(){
        return ENTITIES.keySet().size();
    }

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

    public static int countClassAround(Tile tile, String className) {
        int cnt = 0;
        var x = tile.getX();
        var y = tile.getY();
        for(int i = -1 ; i <= 1 ; i ++) {
            for(int j = -1 ; j < 1 ; j ++) {
                var t = getTileAt(y+i, x+j);
                if(t == null) continue;
                var e = getEntityOver(t);
                if(e == null) continue;
                if(e.getName().equals(className)) cnt ++;
            }
        } return cnt;
    }

    public static int randInt(int upperBound) {
        return new Random().nextInt(upperBound);
    }


}
