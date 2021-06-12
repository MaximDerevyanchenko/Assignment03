package puzzle.rmi.messages;

import akka.actor.typed.ActorRef;

public class GameDataInformMessage extends HandshakeMessage {

    public GameDataInformMessage(){ }

    public GameDataInformMessage(ActorRef<BaseGameMessage> gameActor, String imagePath) {
        super(gameActor, imagePath);
    }
}
