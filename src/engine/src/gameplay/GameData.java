package gameplay;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import grids.Point;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import javafx.event.Event;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

public class GameData {
    static int GRID_WIDTH, GRID_HEIGHT;
    static Map<String, Player> PLAYERS;
    static Map<Integer, Entity> ENTITIES;
    static Map<String, EntityPrototype> ENTITY_PROTOTYPES;
    static Map<Integer, Tile> TILES;
    static Map<String, Phase> PHASES;
    static Map<String, Node> NODES;
    static Set<Edge> EDGES;
    static String WIN_CONDITION;
    static Turn TURN;
    static Pane ROOT;
    static List<ArgumentListener> myArgumentListeners;
    static Initializer myInitializer;

    static GroovyShell shell;

    static Media media;
    static MediaPlayer mediaPlayer;

    public static void setGameData(
            Point grid_dimension, String bgmPath,
            Map<String, Player> players, Map<Integer, Entity> entities,
            Map<String, EntityPrototype> entityPrototypes,
            Map<Integer, Tile> tiles, Map<String, Phase> phases,
            String winCondition, Map<String, Node> nodes,
            Set<Edge> edges, Turn turn, Pane root, Initializer initializer
    ) {
        GameData.GRID_WIDTH = grid_dimension.getX();
        GameData.GRID_HEIGHT = grid_dimension.getY();

        try {
            media = new Media(PathUtility.getResourceAsFile(bgmPath).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
            });
        } catch(Exception e) {
            media = null;
            mediaPlayer = null;
        }

        PLAYERS = players;
        ENTITIES = entities;
        ENTITY_PROTOTYPES = entityPrototypes;
        TILES = tiles;
        PHASES = phases;
        WIN_CONDITION = winCondition;
        NODES = nodes;
        EDGES = edges;
        TURN = turn;
        ROOT = root;
        myArgumentListeners = new ArrayList<>();
        myInitializer = initializer;

        var shared = new Binding();
        shared.setVariable("GameMethods", GameMethods.class);
        shell = new GroovyShell(shared);
    }

    public static GroovyShell shell() {
        return shell;
    }

    public static Map<Integer, Entity> getEntities() {
        return ENTITIES;
    }

    public static Phase getPhase(String phaseName) {
        return PHASES.get(phaseName);
    }

    public static Node getNode(String nodeName) {
        return NODES.get(nodeName);
    }

    public static Map<Integer, Tile> getTiles() {
        return TILES;
    }

    public static Player getPlayer(int playerID) {
        return PLAYERS.get(playerID);
    }

    public static Pane getRoot() {
        return ROOT;
    }

    public static void addArgument(MouseEvent event, ClickTag tag) {
        var target = (tag.getType().equals(Tile.class) ? TILES : ENTITIES).get(tag.getID());
        shell.setVariable("$clicked", target);
        notifyArgumentListeners(event);
    }

    public static void addArgument(KeyEvent event, KeyTag tag) { // todo: connect this with the window
        shell.setVariable("$pressed", tag.code());
        notifyArgumentListeners(event);
    }


    public static void updateViews() {
        ENTITIES.values().forEach(Entity::updateView);
        TILES.values().forEach(Tile::updateView);
    }

    public static Turn getTurn() {
        return TURN;
    }

    public static int getNextEntityID() {
        return ENTITIES.size() + 1;
    }

    public static void addArgumentListener(ArgumentListener argumentListener) {
        myArgumentListeners.add(argumentListener);
    }

    public static void clearArgumentListeners() {
        myArgumentListeners.clear();
    }

    // I really liked your way of having everything pipelined and tried to keep it,
    // but the consequent execution() from the destination node
    // clears/reinitialize the ArgumentListeners. That leads to ConcurrentModificationException
    // So I had to explicitly separate the validity check and execution
    private static void notifyArgumentListeners(Event event) {
        var destination = ArgumentListener.DONT_PASS;
        // at most one of the listeners should pass
        for (ArgumentListener argumentListener : myArgumentListeners) {
            var dest = argumentListener.trigger(event);
            if (!dest.equals(ArgumentListener.DONT_PASS)) destination = dest;
        }
        if (!destination.equals(ArgumentListener.DONT_PASS)) NODES.get(destination).execute();
    }

    public static Collection<Edge> getEdges() {
        return EDGES;
    }

    // TODO: Fix to include all pieces of info
    public static String saveGameData() {
        String xmlString = "";
        XStream serializer = new XStream(new DomDriver());
        xmlString = serializeData(serializer, xmlString, PLAYERS);
        xmlString = serializeData(serializer, xmlString, ENTITIES);
        xmlString = serializeData(serializer, xmlString, ENTITY_PROTOTYPES);
        xmlString = serializeData(serializer, xmlString, TILES);
        xmlString = serializeData(serializer, xmlString, PHASES);
        xmlString = serializeData(serializer, xmlString, NODES);
        xmlString = serializeData(serializer, xmlString, EDGES);
        xmlString = xmlString + serializer.toXML(TURN) + "\n" +
                "<grid-width>" + GRID_WIDTH + "</grid-width>\n" +
                "<grid-height>" + GRID_HEIGHT + "</grid-height>\n" +
                "<winCondition>" + WIN_CONDITION + "</winCondition>\n";
        return xmlString;
    }

    public static String serializeData(XStream serializer, String xmlString, Set<?> dataSet) {
        for (var entry : dataSet) {
            xmlString = xmlString + serializer.toXML(entry) + "\n";
        }
        return xmlString;
    }

    public static String serializeData(XStream serializer, String xmlString, Map<?, ?> dataMap) {
        for (Map.Entry<?, ?> entry : dataMap.entrySet()) {
            xmlString = xmlString + serializer.toXML(entry.getValue()) + "\n";
        }
        return xmlString;
    }

    public static void restartGame() {
        if(mediaPlayer!= null) mediaPlayer.dispose();
        myInitializer.resetRoot();
        myInitializer.initGameData();
        myInitializer.setScreenSize(700, 500);
    }

    public static void stopMusic() {
        mediaPlayer.dispose();
    }
}
