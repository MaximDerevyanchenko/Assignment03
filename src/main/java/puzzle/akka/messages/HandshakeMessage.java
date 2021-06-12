package puzzle.akka.messages;

import akka.actor.typed.ActorRef;

public abstract class HandshakeMessage implements BaseGameMessage {

    private ActorRef<BaseGameMessage> gameActor;
    private String imagePath;

    public HandshakeMessage() { }

    public HandshakeMessage(ActorRef<BaseGameMessage> gameActor, String imagePath){
        this.gameActor = gameActor;
        this.imagePath = imagePath;
    }

    public ActorRef<BaseGameMessage> getGameActor() {
        return this.gameActor;
    }

    public void setGameActor(ActorRef<BaseGameMessage> replyTo) {
        this.gameActor = replyTo;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
