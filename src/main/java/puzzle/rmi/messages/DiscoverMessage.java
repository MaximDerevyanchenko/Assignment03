package puzzle.rmi.messages;

import akka.actor.typed.ActorRef;

public abstract class DiscoverMessage implements BaseMessage {

    private ActorRef<BaseMessage> discoverActor;

    public DiscoverMessage() { }

    public DiscoverMessage(final ActorRef<BaseMessage> discoverActor){
        this.discoverActor = discoverActor;
    }

    public ActorRef<BaseMessage> getDiscoverActor() {
        return this.discoverActor;
    }

    public void setDiscoverActor(final ActorRef<BaseMessage> discoverActor) {
        this.discoverActor = discoverActor;
    }
}
