package puzzle.akka;

import java.util.HashMap;
import java.util.Map;

public class SelectionManager {

	interface Listener {
		void onSwapPerformed(int firstTilePosition, int secondTilePosition);
		void onSelected(String player, int tilePosition);
		void onDeselected(int tilePosition);
	}

	private final String myPlayerName;
	private final Listener listener;
	private final Map<String, Tile> selectedTiles;

	public SelectionManager(String myPlayerName, Listener listener) {
		this.myPlayerName = myPlayerName;
		this.listener = listener;
		this.selectedTiles = new HashMap<>();
	}

	public void selectTile(String player, Tile tile) {
		if (this.selectedTiles.containsKey(player)){
			if (this.selectedTiles.get(player) != tile)
				swap(player, this.selectedTiles.get(player), tile);
			else
				this.listener.onDeselected(tile.getCurrentPosition());
			this.selectedTiles.remove(player);
		} else {
			this.selectedTiles.put(player, tile);
			if (!player.equals(this.myPlayerName))
				this.listener.onSelected(player, tile.getCurrentPosition());
		}
	}

	public void swap(String player, final Tile t1, final Tile t2) {
		this.selectedTiles.remove(player);
		int pos = t1.getCurrentPosition();
		t1.setCurrentPosition(t2.getCurrentPosition());
		t2.setCurrentPosition(pos);
		this.listener.onSwapPerformed(t1.getCurrentPosition(), t2.getCurrentPosition());
	}

	public void removePlayer(String player) {
		if (this.selectedTiles.containsKey(player)) {
			this.listener.onDeselected(this.selectedTiles.get(player).getCurrentPosition());
			this.selectedTiles.remove(player);
		}
	}

	public Map<String, Tile> getSelectedTiles() {
		return this.selectedTiles;
	}

	public String getMyPlayerName() {
		return this.myPlayerName;
	}

	public void setSelectedTiles(Map<String, Tile> selectedTiles) {
		this.selectedTiles.putAll(selectedTiles);
	}
}
