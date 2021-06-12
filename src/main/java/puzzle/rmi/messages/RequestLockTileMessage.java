package puzzle.rmi.messages;

import akka.actor.typed.ActorRef;

public class RequestLockTileMessage extends ClickTileMessage {

    private ActorRef<BaseGameMessage> contender;

    public RequestLockTileMessage() { }

    public RequestLockTileMessage(String player, int tilePosition, ActorRef<BaseGameMessage> contender) {
        super(player, tilePosition);
        this.contender = contender;
    }

    public ActorRef<BaseGameMessage> getContender() {
        return this.contender;
    }

    public void setContender(ActorRef<BaseGameMessage> contender) {
        this.contender = contender;
    }
}
