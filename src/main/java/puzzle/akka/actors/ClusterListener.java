package puzzle.akka.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.ClusterEvent;
import akka.cluster.MemberStatus;
import akka.cluster.typed.*;
import puzzle.akka.messages.BaseGameMessage;
import puzzle.akka.messages.PlayerLeavingMessage;

import java.util.Timer;
import java.util.TimerTask;

public class ClusterListener extends AbstractBehavior<ClusterEvent.ClusterDomainEvent> {

    private final ActorRef<BaseGameMessage> gameActor;

    private ClusterListener(final ActorContext<ClusterEvent.ClusterDomainEvent> context, ActorRef<BaseGameMessage> gameActor) {
        super(context);
        Cluster.get(getContext().getSystem()).subscriptions().tell(Subscribe.create(context.getSelf(), ClusterEvent.ClusterDomainEvent.class));
        this.gameActor = gameActor;
    }

    public static Behavior<ClusterEvent.ClusterDomainEvent> create(ActorRef<BaseGameMessage> gameActor){
        return Behaviors.setup(ctx -> new ClusterListener(ctx, gameActor));
    }

    @Override
    public Receive<ClusterEvent.ClusterDomainEvent> createReceive() {
        return newReceiveBuilder()
                .onMessage(ClusterEvent.MemberExited.class, this::onMemberLeaving)
                .onMessage(ClusterEvent.MemberDowned.class, this::onMemberLeaving)
                .onMessage(ClusterEvent.UnreachableMember.class, this::downMember)
                .build();
    }

    private Behavior<ClusterEvent.ClusterDomainEvent> onMemberLeaving(ClusterEvent.MemberEvent message) {
        this.gameActor.tell(new PlayerLeavingMessage(message.member().address().toString()));
        return Behaviors.same();
    }

    private Behavior<ClusterEvent.ClusterDomainEvent> downMember(ClusterEvent.UnreachableMember message) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (message.member().status() == MemberStatus.down() || Cluster.get(getContext().getSystem()).state().getUnreachable().contains(message.member()))
                    Cluster.get(getContext().getSystem()).manager().tell(Down.apply(message.member().address()));
            }
        }, 20000);
        return Behaviors.same();
    }
}
