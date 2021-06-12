package puzzle.akka.messages;

public abstract class ClickTileMessage implements BaseGameMessage {

    private String player;
    private int tilePosition;

    public ClickTileMessage() { }

    public ClickTileMessage(String player, int tilePosition) {
        this.player = player;
        this.tilePosition = tilePosition;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int getTilePosition() {
        return tilePosition;
    }

    public void setTilePosition(int tilePosition) {
        this.tilePosition = tilePosition;
    }
}
