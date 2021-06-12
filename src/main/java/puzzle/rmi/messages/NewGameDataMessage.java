package puzzle.rmi.messages;

public class NewGameDataMessage extends GameDataMessage {

    public NewGameDataMessage() { }

    public NewGameDataMessage(int rows, int columns, String imagePath) {
        super(rows, columns, imagePath);
    }
}
