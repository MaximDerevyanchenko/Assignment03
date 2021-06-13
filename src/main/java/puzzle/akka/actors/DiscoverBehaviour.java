package puzzle.akka.actors;

import akka.actor.ActorSelection;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.Join;
import puzzle.akka.messages.*;

public class DiscoverBehaviour extends AbstractBehavior<BaseMessage> {

    private final static String SYSTEM = "akka://GameSystem@";
    private final static String PATH = "/user/discover-actor";
    private final ActorRef<BaseMessage> parent;
    private final ActorRef<BaseGameMessage> gameActor;

    private DiscoverBehaviour(ActorContext<BaseMessage> context, ActorRef<BaseMessage> parent) {
        super(context);
        this.parent = parent;
        this.gameActor = getContext().spawn(GameBehaviour.create(), "game-actor");
    }

    public static Behavior<BaseMessage> create(ActorRef<BaseMessage> parent) {
        return Behaviors.setup(context -> new DiscoverBehaviour(context, parent));
    }

    @Override
    public Receive<BaseMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(CreateNewGameMessage.class, this::onCreateNewGameMessage)
                .onMessage(JoinGameMessage.class, this::onJoinGameMessage)
                .onMessage(JoinRequestMessage.class, this::onJoinRequestMessage)
                .onMessage(JoinResponseMessage.class, this::onJoinResponseMessage)
                .build();
    }

    private Behavior<BaseMessage> onCreateNewGameMessage(CreateNewGameMessage createNewGameMessage) {
        this.gameActor.tell(new NewGameDataMessage(createNewGameMessage.getRows(), createNewGameMessage.getColumns(), createNewGameMessage.getImagePath()));
        Cluster cluster = Cluster.get(getContext().getSystem());
        cluster.manager().tell(Join.create(cluster.selfMember().address()));
        return Behaviors.same();
    }

    private Behavior<BaseMessage> onJoinGameMessage(JoinGameMessage joinGameMessage) {
        ActorSelection selection = getContext().classicActorContext().actorSelection(SYSTEM + joinGameMessage.getIpAddress() + ":" + joinGameMessage.getPort() + PATH);
        selection.tell(new JoinRequestMessage(this.gameActor, joinGameMessage.getImagePath(), getContext().getSelf()), akka.actor.ActorRef.noSender());
        return Behaviors.same();
    }

    private Behavior<BaseMessage> onJoinRequestMessage(JoinRequestMessage joinRequestMessage) {
        joinRequestMessage.getDiscover().tell(new JoinResponseMessage(this.parent));
        this.gameActor.tell(joinRequestMessage);
        return Behaviors.same();
    }

    private Behavior<BaseMessage> onJoinResponseMessage(JoinResponseMessage joinResponseMessage) {
        Cluster.get(getContext().getSystem()).manager().tell(Join.create(joinResponseMessage.getDiscoverActor().path().address()));
        return Behaviors.same();
    }
}
