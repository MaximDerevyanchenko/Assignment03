package puzzle.akka.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.pubsub.Topic;
import akka.cluster.Cluster;
import puzzle.akka.PuzzleBoard;
import puzzle.akka.messages.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GameBehaviour extends AbstractBehavior<BaseGameMessage> {

    private final ActorRef<Topic.Command<BaseGameMessage>> topic;
    private final Map<String, String> players;
    private PuzzleBoard game;
    private int answersCounter;
    private boolean canLock;

    private GameBehaviour(ActorContext<BaseGameMessage> context) {
        super(context);
        this.topic = context.spawn(Topic.create(BaseGameMessage.class, "synchronization-topic"), "topic-actor");
        this.topic.tell(Topic.subscribe(context.getSelf()));
        context.spawnAnonymous(ClusterListener.create(getContext().getSelf()));
        this.players = new HashMap<>();
        this.canLock = true;
    }

    public static Behavior<BaseGameMessage> create() {
        return Behaviors.setup(GameBehaviour::new);
    }

    @Override
    public Receive<BaseGameMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(NewGameDataMessage.class, this::createNewGame)
                .onMessage(JoinGameDataMessage.class, this::joinGame)
                .onMessage(JoinRequestMessage.class, this::sendGameData)
                .onMessage(RequestLockTileMessage.class, this::handleLockRequest)
                .onMessage(ResponseLockTileMessage.class, this::handleLockResponse)
                .onMessage(ConfirmLockTileMessage.class, this::selectTile)
                .onMessage(NewPlayerJoinedMessage.class, this::addPlayer)
                .onMessage(PlayerLeavingMessage.class, this::removePlayer)
                .build();
    }

    public ActorRef<Topic.Command<BaseGameMessage>> getTopic() {
        return this.topic;
    }

    private Behavior<BaseGameMessage> createNewGame(NewGameDataMessage newGameDataMessage) {
        String player;
        do {
            player = JOptionPane.showInputDialog(null, "Insert your desired player name: ", "Info", JOptionPane.INFORMATION_MESSAGE);
            if (player == null){
                getContext().getSystem().terminate();
                break;
            }
            if (player.equals(""))
                JOptionPane.showMessageDialog(null, "Could not select empty player name", "Error", JOptionPane.ERROR_MESSAGE);
        } while (player.equals(""));
        if (player != null) {
            this.game = new PuzzleBoard(
                    newGameDataMessage.getRows(),
                    newGameDataMessage.getColumns(),
                    newGameDataMessage.getImagePath(),
                    Cluster.get(getContext().getSystem()).selfAddress().getPort().orElse(0),
                    this,
                    player);
            this.game.setVisible(true);
            this.players.put(Cluster.get(getContext().getSystem()).selfAddress().toString(), player);
        }
        return Behaviors.same();
    }

    private Behavior<BaseGameMessage> joinGame(JoinGameDataMessage puzzleBoardInfo) {
        this.players.putAll(puzzleBoardInfo.getPlayers());
        String player;
        do {
            player = JOptionPane.showInputDialog(null, "Insert your desired player name: ", "Info", JOptionPane.INFORMATION_MESSAGE);
            if (player == null){
                getContext().getSystem().terminate();
                break;
            }
            if (player.equals(""))
                JOptionPane.showMessageDialog(null, "Could not select empty player name!", "Error", JOptionPane.ERROR_MESSAGE);
            if (puzzleBoardInfo.getPlayers().containsValue(player))
                JOptionPane.showMessageDialog(null, "This player name is not available!", "Error", JOptionPane.ERROR_MESSAGE);
        } while (player.equals("") || puzzleBoardInfo.getPlayers().containsValue(player));
        this.topic.tell(Topic.publish(new NewPlayerJoinedMessage(player, Cluster.get(getContext().getSystem()).selfAddress().toString())));
        this.game = new PuzzleBoard(
                puzzleBoardInfo.getRows(),
                puzzleBoardInfo.getColumns(),
                puzzleBoardInfo.getImagePath(),
                Cluster.get(getContext().getSystem()).selfAddress().getPort().orElse(0),
                this,
                puzzleBoardInfo.getTiles(),
                puzzleBoardInfo.getSelectedTiles(),
                new HashSet<>(this.players.values()),
                player);
        this.game.setVisible(true);
        return Behaviors.same();
    }

    private Behavior<BaseGameMessage> sendGameData(JoinRequestMessage gameInfoRequestMessage) {
        gameInfoRequestMessage.getGameActor().tell(new JoinGameDataMessage(
                this.game.getRows(),
                this.game.getColumns(),
                gameInfoRequestMessage.getImagePath(),
                this.game.getTiles(),
                this.game.getSelectedTiles(),
                this.players));
        return Behaviors.same();
    }

    private Behavior<BaseGameMessage> handleLockRequest(RequestLockTileMessage requestLockTileMessage) {
        requestLockTileMessage.getContender().tell(new ResponseLockTileMessage(this.game.canLockTile(requestLockTileMessage.getPlayer(), requestLockTileMessage.getTilePosition()), requestLockTileMessage));
        return Behaviors.same();
    }

    private Behavior<BaseGameMessage> handleLockResponse(ResponseLockTileMessage responseLockTileMessage) {
        this.answersCounter++;
        if (this.answersCounter < this.players.size())
            this.canLock = this.canLock && responseLockTileMessage.canLock();
        else {
            this.answersCounter = 0;
            if (this.canLock)
                this.topic.tell(Topic.publish(new ConfirmLockTileMessage(responseLockTileMessage.getRequest().getPlayer(), responseLockTileMessage.getRequest().getTilePosition())));
            this.game.cancelLock();
            this.canLock = true;
        }
        return Behaviors.same();
    }

    private Behavior<BaseGameMessage> selectTile(ConfirmLockTileMessage clickTileMessage) {
        this.game.selectTile(clickTileMessage.getPlayer(), clickTileMessage.getTilePosition());
        return Behaviors.same();
    }

    private Behavior<BaseGameMessage> addPlayer(NewPlayerJoinedMessage newPlayerJoinedMessage) {
        this.players.put(newPlayerJoinedMessage.getAddress(), newPlayerJoinedMessage.getPlayer());
        this.game.addPlayer(newPlayerJoinedMessage.getPlayer());
        return Behaviors.same();
    }

    private Behavior<BaseGameMessage> removePlayer(PlayerLeavingMessage playerLeavingMessage) {
        if (this.game != null)
            this.game.removePlayer(this.players.get(playerLeavingMessage.getPlayer()));
        this.players.remove(playerLeavingMessage.getPlayer());
        return Behaviors.same();
    }
}
