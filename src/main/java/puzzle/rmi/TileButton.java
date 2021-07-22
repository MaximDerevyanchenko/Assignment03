package puzzle.rmi;

import javax.swing.*;

public class TileButton extends JButton{

	public TileButton(final Tile tile) {
		super(new ImageIcon(tile.getImage()));
		setDisabledIcon(new ImageIcon(tile.getImage()));
	}
}
