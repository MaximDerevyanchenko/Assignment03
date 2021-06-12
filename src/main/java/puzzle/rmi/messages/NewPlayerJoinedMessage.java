package puzzle.rmi.messages;

public class NewPlayerJoinedMessage implements BaseGameMessage {

    private String player;
    private String address;

    public NewPlayerJoinedMessage() { }

    public NewPlayerJoinedMessage(String player, String address) {
        this.player = player;
        this.address = address;
    }

    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
