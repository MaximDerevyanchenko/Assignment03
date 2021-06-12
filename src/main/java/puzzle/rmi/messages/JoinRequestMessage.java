package puzzle.rmi.messages;

import akka.actor.typed.ActorRef;

public class JoinRequestMessage extends DiscoverMessage {

    public JoinRequestMessage() {}

    public JoinRequestMessage(final ActorRef<BaseMessage> discoverActor) {
        super(discoverActor);
    }
}