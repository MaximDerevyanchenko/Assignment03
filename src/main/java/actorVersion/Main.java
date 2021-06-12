package actorVersion;

import akka.actor.typed.ActorSystem;

import javax.swing.*;

public class Main {

    public static void main(final String[] args) {

        ActorSystem.create(GuardianBehaviour.create(), "controller");

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            Thread.sleep(1000);
        } catch (Exception ignored) {}
    }
}
