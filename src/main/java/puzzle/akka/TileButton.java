package puzzle.akka;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class TileButton extends JButton{

	public TileButton(final Tile tile) {
		super(new ImageIcon(tile.getImage()));
		setDisabledIcon(new ImageIcon(tile.getImage()));
	}
}
