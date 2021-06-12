package actorVersion;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class DocumentLoaderBehaviour extends AbstractBehavior<BaseMessage> {

    private static final String PDF_EXTENSION = ".pdf";
    private String document = "";

    private DocumentLoaderBehaviour(ActorContext<BaseMessage> context) {
        super(context);
    }

    public static Behavior<BaseMessage> create(){
        return Behaviors.setup(DocumentLoaderBehaviour::new);
    }

    @Override
    public Receive<BaseMessage> createReceive() {
        return newReceiveBuilder().onMessage(LoadingMessage.class, this::onLoadingMessage).build();
    }

    private Behavior<BaseMessage> onLoadingMessage(LoadingMessage loadingMessage) {
        this.load(loadingMessage.getFile());
        loadingMessage.getReplyTo().tell(new DocumentMessage(this.document, this.getContext().getSelf()));
        return Behaviors.stopped();
    }

    private void load(File file) {
        if (file.isFile() && isPDF(file)){
            try {
                final PDDocument documentPDF = PDDocument.load(file);
                final AccessPermission ap = documentPDF.getCurrentAccessPermission();
                if (!ap.canExtractContent())
                    throw new IOException("You do not have permission to extract text");

                final PDFTextStripper stripper = new PDFTextStripper();
                stripper.setPageEnd("\254");
                this.document = stripper.getText(documentPDF);
                documentPDF.close();
            } catch (IOException e) {
                System.out.println("Document not loaded correctly");
            }
        }
    }

    private boolean isPDF(final File file) {
        final String filename = file.getName();
        return filename.substring(filename.lastIndexOf(".")).equals(PDF_EXTENSION);
    }

}
