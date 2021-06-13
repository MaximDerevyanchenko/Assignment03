package puzzle.akka.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinGameDataMessage extends GameDataMessage {

    private List<TileInfoMessage> tiles = new ArrayList<>();
    private Map<String, TileInfoMessage> selectedTiles = new HashMap<>();
    private Map<String, String> players;

    public JoinGameDataMessage() { }

    public JoinGameDataMessage(int rows, int columns, String imagePath, List<TileInfoMessage> tiles, Map<String, TileInfoMessage> selectedTiles, Map<String, String> players) {
        super(rows, columns, imagePath);
        this.tiles = tiles;
        this.selectedTiles = selectedTiles;
        this.players = players;
    }

    public void setTiles(List<TileInfoMessage> tiles) {
        this.tiles = tiles;
    }

    public List<TileInfoMessage> getTiles() {
        return this.tiles;
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
