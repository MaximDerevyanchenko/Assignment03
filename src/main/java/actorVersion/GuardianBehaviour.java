package actorVersion;

import akka.actor.AbstractActor;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GuardianBehaviour extends AbstractBehavior<BaseMessage> {

    private static final String EXCLUDING_REGEX = "[^a-zA-Z\254\s\n]";
    private static final String ESCAPE_SEQUENCE_SEPARATOR = "\254";
    public static final int NUMBER_OF_PAGES = 20;
    private int maxOccurrencesAmount;
    private final List<String> excludedWords = new ArrayList<>();
    private List<File> files = new ArrayList<>();
    private GUI gui;
    private String documents = "";
    private final ActorContext<BaseMessage> context;
    private final List<ActorRef<BaseMessage>> children;
    private Result result;

    public GuardianBehaviour(ActorContext<BaseMessage> context){
        super(context);
        this.children = new ArrayList<>();
        this.result = new Result();
        this.context = context;
    }

    @Override
    public Receive<BaseMessage> createReceive() {
        //4 messages: start, loaded, update, stop
        return newReceiveBuilder()
                .onMessage(StartMessage.class, this::onStartMessage)
                .onMessage(DocumentMessage.class, this::ManageDocuments)
                .onMessage(ResultMessage.class, this::onResultMessage)
                .onMessage(StopMessage.class, this::onStopMessage)
                .onSignal(Terminated.class, this::onTerminated)
                .build();
    }

    private Behavior<BaseMessage> onTerminated(Terminated sig) {
        return this;
    }

    private Behavior<BaseMessage> onResultMessage(ResultMessage resultMsg) {
        this.result.assemble(resultMsg.getResult());
        this.updateUI();
        this.children.remove(resultMsg.getChildRef());
        if (this.children.isEmpty()) {
            this.gui.resetButtons();
        }
        return this;
    }

    private Behavior<BaseMessage> ManageDocuments(DocumentMessage doc) {
        this.documents = this.documents.concat(doc.getDocument());
        this.children.remove(doc.getChildRef());
        if (this.children.isEmpty())
            this.analyzeDocuments();
        return this;
    }

    private void analyzeDocuments() {
        final Scanner scanner = new Scanner(this.documents.replaceAll(EXCLUDING_REGEX, ""));
        scanner.useDelimiter(ESCAPE_SEQUENCE_SEPARATOR);
        List<String> pages = scanner.tokens().collect(Collectors.toList());
        int totalPages = pages.size();
        scanner.close();
        int i = 0, actualStartPageNumber = 0;
        do {
            final int endPageNumber = actualStartPageNumber + NUMBER_OF_PAGES;
            List<String> pagesToProcess = pages.subList(actualStartPageNumber, Math.min(endPageNumber, totalPages));
            ActorRef<BaseMessage> child = context.spawn(DocumentReaderBehaviour.create(this.excludedWords), "reader-" + i++);
            this.children.add(child);
            child.tell(new ReadingMessage(pagesToProcess, this.context.getSelf()));
            this.context.watch(child);
            actualStartPageNumber = endPageNumber;
        } while (actualStartPageNumber < totalPages);
    }

    public static Behavior<BaseMessage> create(){
        return Behaviors.setup(ctx -> {
            GuardianBehaviour c =  new GuardianBehaviour(ctx);
            final GUI gui = new GUI(ctx.getSelf());
            c.setView(gui);
            SwingUtilities.invokeLater(() -> gui.setVisible(true));
            return c;
        });
    }

    public void setArguments(final StartMessage startMessage) {
        this.maxOccurrencesAmount = startMessage.getWordsAmount();
        File dir = new File(startMessage.getPdfDirectory());
        if (dir.exists() && dir.isDirectory()) {
            try {
                BufferedReader buffer = new BufferedReader(new FileReader(startMessage.getExcludedFile()));
                while (buffer.ready())
                    this.excludedWords.add(buffer.readLine());
            } catch (IOException ignored) { }
            this.files = Arrays.asList(Objects.requireNonNull(dir.listFiles()));
        }
    }

    public Behavior<BaseMessage> onStartMessage(StartMessage msg) {
        this.result = new Result();
        this.documents = "";

        setArguments(msg);
        int i = 0;
        for (File file : files) {
            ActorRef<BaseMessage> child = context.spawn(DocumentLoaderBehaviour.create(), "loader-" + i++);
            this.children.add(child);
            child.tell(new LoadingMessage(file, this.context.getSelf()));
            this.context.watch(child);
        }

        return this;
    }

    public Behavior<BaseMessage> onStopMessage(StopMessage stopMessage){
        this.children.stream().peek(context::stop).close();
        this.children.clear();
        this.gui.resetButtons();
        return this;
    }

    public void setView(GUI gui) {
        this.gui = gui;
    }

    private void updateUI() {
        final Map<String, Integer> globalMap = this.result.getMap();
        int processedWords = this.result.getProcessedWords();

        final List<String> result = takeMax(globalMap, maxOccurrencesAmount);
        final Map<String, Integer> resultMap = new HashMap<>();
        for (String r : result)
            resultMap.put(r, globalMap.get(r));

        final List<String> output = new ArrayList<>();

        for (int i = 0; i < this.maxOccurrencesAmount && !resultMap.isEmpty(); i++) {
            final Map.Entry<String, Integer> res = Collections.max(resultMap.entrySet(), Map.Entry.comparingByValue());
            output.add(res.getKey() + ": " + res.getValue());
            resultMap.remove(res.getKey());
        }

        gui.updateCountValue(output, processedWords);
    }

    private List<String> takeMax(final Map<String, Integer> globalMap, int n){
        List<String> result = new ArrayList<>();
        int min = 0;
        String minKey = "";
        for (String key: globalMap.keySet()){
            if (min == 0){
                minKey = key;
                min = globalMap.get(key);
            }
            if (result.size() < n) {
                result.add(key);
                if (globalMap.get(key) < min) {
                    minKey = key;
                    min = globalMap.get(key);
                }
            } else {
                if (globalMap.get(key) > min) {
                    result.remove(minKey);
                    result.add(key);
                    minKey = result.get(0);
                    min = globalMap.get(minKey);
                    for (String k : result) {
                        if (globalMap.get(k) < min){
                            minKey = k;
                            min = globalMap.get(k);
                        }
                    }
                }
            }
        }
        return result;
    }
}
