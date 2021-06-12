package puzzle.akka.messages;

public class TileInfoMessage implements BaseGameMessage {

    private int originalPosition;
    private int currentPosition;

    public TileInfoMessage(int originalPosition, int currentPosition) {
        this.originalPosition = originalPosition;
        this.currentPosition = currentPosition;
    }

    public int getOriginalPosition() {
        return originalPosition;
    }

    public void setOriginalPosition(int originalPosition) {
        this.originalPosition = originalPosition;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
