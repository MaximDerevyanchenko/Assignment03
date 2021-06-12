package actorVersion;


import akka.actor.typed.ActorRef;

import java.io.File;

public class LoadingMessage implements BaseMessage {

    private final File file;
    private final ActorRef<BaseMessage> replyTo;

    public LoadingMessage(File file, ActorRef<BaseMessage> replyTo){
        this.file = file;
        this.replyTo = replyTo;
    }

    public File getFile() {
        return file;
    }

    public ActorRef<BaseMessage> getReplyTo() {
        return replyTo;
    }
}
