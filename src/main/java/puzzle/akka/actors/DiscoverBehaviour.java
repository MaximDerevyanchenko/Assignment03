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
    private ActorRef<BaseGameMessage> gameActor;
    private String imagePath;

    private DiscoverBehaviour(ActorContext<BaseMessage> context, ActorRef<BaseMessage> parent) {
        super(context);
        this.parent = parent;
    }

    public static Behavior<BaseMessage> create(ActorRef<BaseMessage> parent) {
        return Behaviors.setup(context -> new DiscoverBehaviour(context, parent));
    }

    @Override
    public Receive<BaseMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(JoinRequestMessage.class, this::onJoinRequestMessage)
                .onMessage(JoinResponseMessage.class, this::onJoinResponseMessage)
                .onMessage(JoinGameMessage.class, this::onJoinGameMessage)
                .build();
    }

    private Behavior<BaseMessage> onJoinGameMessage(JoinGameMessage joinGameMessage) {
        if (joinGameMessage.getPort() != 0) {
            this.imagePath = joinGameMessage.getImagePath();
            ActorSelection selection = getContext().classicActorContext().actorSelection(SYSTEM + joinGameMessage.getIpAddress() + ":" + joinGameMessage.getPort() + PATH);
            selection.tell(new JoinRequestMessage(getContext().getSelf()), akka.actor.ActorRef.noSender());
        } else {
            this.gameActor = getContext().spawn(GameBehaviour.create(), "game-actor");
            this.gameActor.tell(new NewGameDataMessage(joinGameMessage.getRows(), joinGameMessage.getColumns(), joinGameMessage.getImagePath()));
            Cluster cluster = Cluster.get(getContext().getSystem());
            cluster.manager().tell(Join.create(cluster.selfMember().address()));
        }
        return Behaviors.same();
    }

    private Behavior<BaseMessage> onJoinRequestMessage(JoinRequestMessage joinRequestMessage) {
        joinRequestMessage.getDiscoverActor().tell(new JoinResponseMessage(this.parent, this.gameActor));
        return Behaviors.same();
    }

    private Behavior<BaseMessage> onJoinResponseMessage(JoinResponseMessage joinResponseMessage) {
        this.gameActor = getContext().spawn(GameBehaviour.create(), "game-actor");
        Cluster.get(getContext().getSystem()).manager().tell(Join.create(joinResponseMessage.getDiscoverActor().path().address()));
        this.gameActor.tell(new GameDataInformMessage(joinResponseMessage.getGameActor(), this.imagePath));
        return Behaviors.same();
    }
}
