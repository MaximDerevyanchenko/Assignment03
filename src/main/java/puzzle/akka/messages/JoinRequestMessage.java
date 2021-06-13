package puzzle.akka.messages;

import akka.actor.typed.ActorRef;

public class JoinRequestMessage implements BaseGameMessage {

    private ActorRef<BaseGameMessage> gameActor;
    private String imagePath;
    private ActorRef<BaseMessage> discover;

    public JoinRequestMessage() { }

    public JoinRequestMessage(ActorRef<BaseGameMessage> gameActor, String imagePath, ActorRef<BaseMessage> discover){
        this.gameActor = gameActor;
        this.imagePath = imagePath;
        this.discover = discover;
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

    public ActorRef<BaseMessage> getDiscover() {
        return this.discover;
    }

    public void setDiscover(ActorRef<BaseMessage> discover) {
        this.discover = discover;
    }
}
