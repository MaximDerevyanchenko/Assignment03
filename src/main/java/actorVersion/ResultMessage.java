package actorVersion;

import akka.actor.typed.ActorRef;

public class ResultMessage implements BaseMessage {

    private final Result result;
    private final ActorRef<BaseMessage> childRef;

    public ResultMessage(Result result, ActorRef<BaseMessage> childRef) {
        this.result = result;
        this.childRef = childRef;
    }

    public Result getResult() {
        return result;
    }

    public ActorRef<BaseMessage> getChildRef() {
        return childRef;
    }
}
