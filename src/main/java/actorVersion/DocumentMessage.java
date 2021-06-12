package actorVersion;

import akka.actor.typed.ActorRef;

public class DocumentMessage implements BaseMessage {

    private final String document;
    private final ActorRef<BaseMessage> childRef;

    public DocumentMessage(String document, ActorRef<BaseMessage> childRef) {
        this.document = document;
        this.childRef = childRef;
    }

    public String getDocument() {
        return document;
    }

    public ActorRef<BaseMessage> getChildRef() {
        return childRef;
    }
}
