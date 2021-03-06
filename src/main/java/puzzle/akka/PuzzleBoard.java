package puzzle.akka;

import akka.actor.typed.pubsub.Topic;
import puzzle.akka.actors.GameBehaviour;
import puzzle.akka.messages.RequestLockTileMessage;
import puzzle.akka.messages.TileInfoMessage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PuzzleBoard extends JFrame {

    private static final int THICKNESS = 1;
    private final int rows;
    private final int columns;
    private final GameBehaviour gameBehaviour;
    private final List<Tile> tiles = new ArrayList<>();
	private final JPanel board;
    private final SelectionManager selectionManager;
    private final Map<String, Color> colors;
    private final List<Color> colorsList;
    private int colorCounter;
    private int imageWidth;
    private int imageHeight;
    private BufferedImage image;
	private Optional<Tile> mySelectedTile;

    private PuzzleBoard(final int rows, final int columns, final String imagePath, final GameBehaviour gameBehaviour, String myNickname){
        this.rows = rows;
        this.columns = columns;
        this.gameBehaviour = gameBehaviour;
        this.colors = new HashMap<>();
        this.mySelectedTile = Optional.empty();
        this.colorsList = new ArrayList<>();
        this.colorsList.addAll(Arrays.asList(Color.green, Color.blue, Color.magenta, Color.cyan, Color.yellow, Color.black, Color.pink));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.board = new JPanel();
        this.board.setBorder(BorderFactory.createLineBorder(Color.gray));

        this.board.setLayout(new GridLayout(rows, columns, 0, 0));
        getContentPane().add(this.board, BorderLayout.CENTER);

        SelectionManager.Listener listener = new SelectionManager.Listener() {
            @Override
            public void onSwapPerformed(int firstTilePosition, int secondTilePosition) {
                paintPuzzle();
                checkSolution();
            }

            @Override
            public void onSelected(String player,int tilePosition) {
                select(player, tilePosition);
            }

            @Override
            public void onDeselected(int tilePosition) {
                deselect(tilePosition);
            }
        };

        this.selectionManager = new SelectionManager(myNickname, listener);

        try {
            this.image = ImageIO.read(new File(imagePath));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load image", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.imageWidth = this.image.getWidth(null);
        this.imageHeight = this.image.getHeight(null);
    }

    public PuzzleBoard(final int rows, final int columns, final String imagePath, final int port, final GameBehaviour gameBehaviour, final String myNickname) {
	    this(rows, columns, imagePath, gameBehaviour, myNickname);

        setTitle("Puzzle " + port);
        createTiles();
        paintPuzzle();
    }

    public PuzzleBoard(final int rows, final int columns, final String imagePath, final int port, final GameBehaviour gameBehaviour, final List<TileInfoMessage> tiles, Map<String, TileInfoMessage> selectedTiles, final Set<String> players, final String myNickname) {
        this(rows, columns, imagePath, gameBehaviour, myNickname);
        setTitle("Puzzle " + port);

        this.tiles.addAll(tiles.stream().map(this::createTileFromMessage).collect(Collectors.toList()));

        this.selectionManager.setSelectedTiles(selectedTiles.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), this.tiles.get(entry.getValue().getCurrentPosition())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        players.forEach(player -> this.colors.put(player, this.colorsList.get(this.colorCounter++ % this.colorsList.size())));

        paintPuzzle();
    }

    public void selectTile(String player, int tilePosition) {
        this.selectionManager.selectTile(player, this.tiles.get(tilePosition));
    }

    public void addPlayer(String player) {
        this.colors.put(player, this.colorsList.get(this.colorCounter++ % this.colorsList.size()));
    }

    public void removePlayer(String player){
        this.selectionManager.removePlayer(player);
        this.colors.remove(player);
    }

    public boolean canLockTile(String player, int tilePosition){
        return player.compareTo(this.selectionManager.getMyPlayerName()) <= 0 || this.mySelectedTile.isEmpty() || this.mySelectedTile.get().getCurrentPosition() != tilePosition;
    }

    public void cancelLock() {
        this.mySelectedTile = Optional.empty();
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public List<TileInfoMessage> getTiles() {
        return this.tiles.stream().map(tile -> new TileInfoMessage(tile.getOriginalPosition(), tile.getCurrentPosition())).collect(Collectors.toList());
    }

    public Map<String, TileInfoMessage> getSelectedTiles() {
        return this.selectionManager.getSelectedTiles().entrySet().stream().map(entry ->
                new AbstractMap.SimpleEntry<>(entry.getKey(), new TileInfoMessage(entry.getValue().getOriginalPosition(), entry.getValue().getCurrentPosition())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Tile createTileFromMessage(TileInfoMessage tile) {
        int i = tile.getOriginalPosition() / this.columns;
        int j = tile.getOriginalPosition() % this.columns;
        final Image imagePortion = createImage(new FilteredImageSource(image.getSource(),
                new CropImageFilter(j * imageWidth / columns,
                        i * imageHeight / rows,
                        (imageWidth / columns),
                        imageHeight / rows)));
        return new Tile(imagePortion, tile.getOriginalPosition(), tile.getCurrentPosition());
    }

    private void paintPuzzle() {
        Collections.sort(this.tiles);
        this.board.removeAll();

        this.tiles.forEach(tile -> {
            final TileButton btn = new TileButton(tile);
            board.add(btn);
            if (selectionManager.getSelectedTiles().containsValue(tile)){
                String playerName = selectionManager.getSelectedTiles().entrySet().stream().filter(e -> e.getValue().equals(tile)).findFirst().orElseThrow().getKey();
                btn.setToolTipText(playerName);
                if (!playerName.equals(this.selectionManager.getMyPlayerName())) {
                    btn.setBorder(BorderFactory.createLineBorder(this.colors.get(playerName), THICKNESS));
                    btn.setEnabled(false);
                }
                else
                    btn.setBorder(BorderFactory.createLineBorder(Color.red, THICKNESS));
            } else
                btn.setBorder(BorderFactory.createLineBorder(Color.gray, THICKNESS));
            btn.addActionListener(actionListener -> {
                if (this.selectionManager.getSelectedTiles().containsKey(this.selectionManager.getMyPlayerName()))
                    btn.setBorder(BorderFactory.createLineBorder(Color.gray, THICKNESS));
                else {
                    btn.setToolTipText(this.selectionManager.getMyPlayerName());
                    btn.setBorder(BorderFactory.createLineBorder(Color.red, THICKNESS));
                }
                this.mySelectedTile = Optional.of(tile);
                this.gameBehaviour.getTopic().tell(Topic.publish(new RequestLockTileMessage(this.selectionManager.getMyPlayerName(), tile.getCurrentPosition(), this.gameBehaviour.getContext().getSelf())));
            });
        });
        pack();
    }

    private void createTiles() {
        int position = 0;
        final List<Integer> randomPositions = new ArrayList<>();
        IntStream.range(0, rows*columns).forEach(randomPositions::add);
        Collections.shuffle(randomPositions);
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
            	final Image imagePortion = createImage(new FilteredImageSource(image.getSource(),
                        new CropImageFilter(j * imageWidth / columns, 
                        					i * imageHeight / rows, 
                        					(imageWidth / columns), 
                        					imageHeight / rows)));

                tiles.add(new Tile(imagePortion, position, randomPositions.get(position)));
                position++;
            }
        }
	}

    private void select(String player, int tilePosition) {
        TileButton tileButton = (TileButton) this.board.getComponent(tilePosition);
        tileButton.setEnabled(false);
        tileButton.setBorder(BorderFactory.createLineBorder(this.colors.get(player), THICKNESS));
        tileButton.setToolTipText(player);
    }

    private void deselect(int tilePosition) {
        TileButton tileButton = ((TileButton) this.board.getComponent(tilePosition));
        tileButton.setBorder(BorderFactory.createLineBorder(Color.gray, THICKNESS));
        tileButton.setToolTipText(null);
        tileButton.setEnabled(true);
    }

    private void checkSolution() {
    	if (this.tiles.stream().allMatch(Tile::isInRightPlace))
    		JOptionPane.showMessageDialog(this, "Puzzle Completed!", "", JOptionPane.INFORMATION_MESSAGE);
    }

}
