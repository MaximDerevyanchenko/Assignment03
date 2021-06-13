package puzzle.akka.messages;

public class JoinGameMessage implements BaseGameMessage {

    private String imagePath;
    private String ipAddress;
    private int port;

    public JoinGameMessage() { }

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
}
