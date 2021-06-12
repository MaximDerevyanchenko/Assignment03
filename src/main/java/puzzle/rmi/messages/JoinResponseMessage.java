package puzzle.rmi.messages;

import akka.actor.typed.ActorRef;

public class JoinResponseMessage extends DiscoverMessage {

    private ActorRef<BaseGameMessage> gameActor;

    public JoinResponseMessage(){ }

    public JoinResponseMessage(ActorRef<BaseMessage> discoverActor, ActorRef<BaseGameMessage> gameActor) {
        super(discoverActor);
        this.gameActor = gameActor;
    }

    public ActorRef<BaseGameMessage> getGameActor() {
        return this.gameActor;
    }

    public void setGameActor(ActorRef<BaseGameMessage> gameActor) {
        this.gameActor = gameActor;
    }
}
