package puzzle.akka.messages;

public class CreateNewGameMessage implements BaseGameMessage {

    private String imagePath;
    private int rows, columns;

    public CreateNewGameMessage() {}

    public CreateNewGameMessage(final String imagePath, int rows, int columns){
        this.imagePath = imagePath;
        this.rows = rows;
        this.columns = columns;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(final String imagePath) {
        this.imagePath = imagePath;
    }

    public int getRows() {
        return this.rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
}
