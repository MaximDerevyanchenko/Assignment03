package puzzle.akka.messages;

import akka.actor.typed.ActorRef;

public class JoinResponseMessage extends DiscoverMessage {

    public JoinResponseMessage(){ }

    public JoinResponseMessage(ActorRef<BaseMessage> discoverActor) {
        super(discoverActor);
    }
}
