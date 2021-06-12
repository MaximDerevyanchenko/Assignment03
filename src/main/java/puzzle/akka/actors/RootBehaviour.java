package puzzle.akka.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import puzzle.akka.StartGame;
import puzzle.akka.messages.BaseMessage;
import puzzle.akka.messages.CreateNewGameMessage;
import puzzle.akka.messages.JoinGameMessage;

import javax.swing.*;

public class RootBehaviour extends AbstractBehavior<BaseMessage> {

    private final ActorRef<BaseMessage> discoverActor;

    private RootBehaviour(ActorContext<BaseMessage> context) {
        super(context);
        this.discoverActor = context.spawn(DiscoverBehaviour.create(context.getSelf()), "discover-actor");
        JFrame startFrame = new StartGame(context.getSelf());
        startFrame.setVisible(true);
    }

    public static Behavior<BaseMessage> create() {
        return Behaviors.setup(RootBehaviour::new);
    }

    @Override
    public Receive<BaseMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(JoinGameMessage.class, this::onJoinGame)
                .onMessage(CreateNewGameMessage.class, this::onNewGame)
                .build();
    }

    private Behavior<BaseMessage> onNewGame(CreateNewGameMessage newGame) {
        this.discoverActor.tell(new JoinGameMessage(newGame.getImagePath(), newGame.getRows(), newGame.getColumns()));
        return Behaviors.same();
    }

    private Behavior<BaseMessage> onJoinGame(JoinGameMessage joinGameMessage) {
        this.discoverActor.tell(joinGameMessage);
        return Behaviors.same();
    }
}
