package actorVersion;

import java.util.HashMap;
import java.util.Map;

public class Result {
    private final Map<String, Integer> map;
    private int processedWords;

    public Result() {
        this.map = new HashMap<>();
        this.processedWords = 0;
    }

    public Result(Map<String, Integer> map, int processedWords) {
        this.map = map;
        this.processedWords = processedWords;
    }

    public void assemble(Result result) {
        processedWords += result.getProcessedWords();
        result.getMap().forEach((word, occurrences) -> this.map.merge(word, occurrences, (localOccurrences, globalOccurrences) -> globalOccurrences += localOccurrences));
    }

    public int getProcessedWords() {
        return this.processedWords;
    }

    public Map<String, Integer> getMap() {
        return this.map;
    }
}