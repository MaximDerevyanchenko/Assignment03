package actorVersion;

import akka.actor.typed.ActorRef;

import java.util.List;

public class ReadingMessage implements BaseMessage {

    private final List<String> pages;
    private final ActorRef<BaseMessage> replyTo;

    public ReadingMessage(List<String> pages, ActorRef<BaseMessage> replyTo) {
        this.pages = pages;
        this.replyTo = replyTo;
    }

    public List<String> getPages() {
        return pages;
    }

    public ActorRef<BaseMessage> getReplyTo() {
        return replyTo;
    }
}
