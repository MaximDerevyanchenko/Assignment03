package puzzle.rmi.messages;

public class JoinGameMessage implements BaseGameMessage {

    private String imagePath;
    private String ipAddress;
    private int port;
    private int rows, columns;

    public JoinGameMessage() { }

    public JoinGameMessage(final String imagePath, int rows, int columns) {
        this.imagePath = imagePath;
        this.rows = rows;
        this.columns = columns;
    }

    public JoinGameMessage(final String imagePath, final String ipAddress, final int port){
        this.imagePath = imagePath;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
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
