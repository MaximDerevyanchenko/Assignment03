package puzzle.rmi.messages;

public class ConfirmLockTileMessage extends ClickTileMessage {

    public ConfirmLockTileMessage() { }

    public ConfirmLockTileMessage(String player, int tilePosition) {
        super(player, tilePosition);
    }
}
