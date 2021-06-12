package actorVersion;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.*;

public class DocumentReaderBehaviour extends AbstractBehavior<BaseMessage> {

    private final List<String> excludedWords;
    private Result result;

    private DocumentReaderBehaviour(ActorContext<BaseMessage> context, List<String> excludedWords) {
        super(context);
        this.excludedWords = excludedWords;
    }

    public static Behavior<BaseMessage> create(List<String> excludedWords){
        return Behaviors.setup(context -> new DocumentReaderBehaviour(context, excludedWords));
    }

    @Override
    public Receive<BaseMessage> createReceive() {
        return newReceiveBuilder().onMessage(ReadingMessage.class, this::onReadingMessage).build();
    }

    private Behavior<BaseMessage> onReadingMessage(ReadingMessage msg) {
        this.analyzePage(msg.getPages());
        msg.getReplyTo().tell(new ResultMessage(this.result, this.getContext().getSelf()));
        return Behaviors.stopped();
    }

    public void analyzePage(List<String> page){
        Scanner scanner = new Scanner(page.stream().reduce(String::concat).orElse(""));
        int processedWords = 0;
        Map<String, Integer> wordsFrequencies = new HashMap<>();
        while (scanner.hasNext()) {
            String word = scanner.next();
            if (!isExcluded(word)) {
                processedWords++;
                if (!wordsFrequencies.containsKey(word))
                    wordsFrequencies.put(word, 1);
                else
                    wordsFrequencies.put(word, wordsFrequencies.get(word) + 1);
            }
        }
        scanner.close();
        this.result = new Result(wordsFrequencies, processedWords);
    }

    private boolean isExcluded(String word) {
        return this.excludedWords.contains(word);
    }
}
