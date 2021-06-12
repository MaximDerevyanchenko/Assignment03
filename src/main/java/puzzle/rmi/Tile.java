package puzzle.rmi;

import java.awt.*;
import java.io.Serializable;

public class Tile implements Comparable<Tile>, Serializable {

	private transient Image image;
	private final int originalPosition;
	private int currentPosition;

    public Tile(final Image image, final int originalPosition, final int currentPosition) {
        this.image = image;
        this.originalPosition = originalPosition;
        this.currentPosition = currentPosition;
    }
    
    public Image getImage() {
    	return image;
    }
    
    public boolean isInRightPlace() {
    	return currentPosition == originalPosition;
    }

    public int getOriginalPosition() {
        return this.originalPosition;
    }

    public int getCurrentPosition() {
    	return currentPosition;
    }
    
    public void setCurrentPosition(final int newPosition) {
    	currentPosition = newPosition;
    }

	@Override
	public int compareTo(Tile other) {
		return Integer.compare(this.currentPosition, other.currentPosition);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        return getCurrentPosition() == tile.getCurrentPosition();
    }

    @Override
    public int hashCode() {
        return getCurrentPosition();
    }

    public void setImage(Image imagePortion) {
        this.image = imagePortion;
    }
}
