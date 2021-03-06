package puzzle.akka;

import akka.actor.typed.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import puzzle.akka.actors.RootBehaviour;

import java.io.File;

public class Peer {

	public static void main(final String[] args) {
		Config config = ConfigFactory.parseFile(new File("src/main/java/puzzle/akka/application.conf"));
		ActorSystem.create(RootBehaviour.create(),"GameSystem", config);
	}
}
