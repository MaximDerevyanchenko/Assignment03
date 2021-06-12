package puzzle.akka.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinGameDataMessage extends GameDataMessage {

    private List<TileInfoMessage> tiles = new ArrayList<>();
    private Map<String, TileInfoMessage> selectedTiles = new HashMap<>();
    private Map<String, String> players;
    private String player;

    public JoinGameDataMessage() { }

    public JoinGameDataMessage(int rows, int columns, String imagePath, List<TileInfoMessage> tiles, Map<String, TileInfoMessage> selectedTiles, Map<String, String> players, String player) {
        super(rows, columns, imagePath);
        this.tiles = tiles;
        this.selectedTiles = selectedTiles;
        this.players = players;
        this.player = player;
    }

    public void setTiles(List<TileInfoMessage> tiles) {
        this.tiles = tiles;
    }

    public List<TileInfoMessage> getTiles() {
        return this.tiles;
    }

    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Map<String, String> getPlayers() {
        return this.players;
    }

    public void setPlayers(Map<String, String> players) {
        this.players = players;
    }

    public Map<String, TileInfoMessage> getSelectedTiles() {
        return this.selectedTiles;
    }

    public void setSelectedTiles(Map<String, TileInfoMessage> selectedTiles) {
        this.selectedTiles = selectedTiles;
    }
}
