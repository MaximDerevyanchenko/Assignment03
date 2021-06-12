package puzzle.rmi.messages;

public abstract class GameDataMessage implements BaseGameMessage {

    private String imagePath;
    private int rows, columns;

    public GameDataMessage() {}

    public GameDataMessage(int rows, int columns, String imagePath) {
        this.rows = rows;
        this.columns = columns;
        this.imagePath = imagePath;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
