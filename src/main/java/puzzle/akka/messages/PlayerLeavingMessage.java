package puzzle.akka.messages;

public class PlayerLeavingMessage implements BaseGameMessage {

    private String player;

    public PlayerLeavingMessage() { }

    public PlayerLeavingMessage(String address) {
        this.player = address;
    }

    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
