package actorVersion;

public class StartMessage implements BaseMessage {

    private final String pdfDirectory;
    private final int wordsAmount;
    private final String excludedFile;

    public StartMessage(String pdfDirectory, int wordsAmount, String excludedFile) {
        this.pdfDirectory = pdfDirectory;
        this.wordsAmount = wordsAmount;
        this.excludedFile = excludedFile;
    }

    public String getPdfDirectory() {
        return pdfDirectory;
    }

    public int getWordsAmount() {
        return wordsAmount;
    }

    public String getExcludedFile() {
        return excludedFile;
    }
}
