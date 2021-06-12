package puzzle.akka.messages;

public class ResponseLockTileMessage implements BaseGameMessage {

    private boolean canLock;
    private RequestLockTileMessage request;

    public ResponseLockTileMessage() { }

    public ResponseLockTileMessage(boolean canLock, RequestLockTileMessage request) {
        this.canLock = canLock;
        this.request = request;
    }

    public boolean canLock() {
        return this.canLock;
    }

    public void setCanLock(boolean canLock) {
        this.canLock = canLock;
    }

    public RequestLockTileMessage getRequest() {
        return this.request;
    }

    public void setRequest(RequestLockTileMessage request) {
        this.request = request;
    }

}
