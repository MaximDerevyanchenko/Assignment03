package puzzle.akka.messages;

import akka.actor.typed.ActorRef;

public class GameDataRequestMessage extends HandshakeMessage {

    private String player;

    public GameDataRequestMessage(){ }

    public GameDataRequestMessage(ActorRef<BaseGameMessage> gameActor, String imagePath, String player) {
       super(gameActor, imagePath);
       this.player = player;
    }

    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
}
