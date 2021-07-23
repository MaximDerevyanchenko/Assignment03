package puzzle.rmi;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class GameServiceImpl implements GameService {

    private int rows;
    private int columns;
    private transient PuzzleBoard game;
    private final Map<GameService, String> otherPlayersBoards = new HashMap<>();
    private String player;

    @Override
    public void startNewGame(String imagePath, int rows, int columns, int myPort) throws RemoteException {
        do {
            initPlayerName();
        } while (this.player.equals(""));
        this.rows = rows;
        this.columns = columns;
        this.game = new PuzzleBoard(
                rows,
                columns,
                imagePath,
                myPort,
                player,
                this);
        this.game.setVisible(true);
    }

    @Override
    public void joinGame(String imagePath, String host, int joinPort, int myPort) throws RemoteException {
        final GameService gameService;
        final List<Tile> tiles;
        final Map<String,Tile> selectedTiles;
        try {
            Registry registry = LocateRegistry.getRegistry(host, joinPort);
            gameService = (GameService) registry.lookup(String.valueOf(joinPort));
            this.otherPlayersBoards.putAll(gameService.getCurrentPlayers());
            this.rows = gameService.getRows();
            this.columns = gameService.getColumns();
            tiles = gameService.getTiles();
            selectedTiles = gameService.getSelectedTiles();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Remote connection lost.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }
        do {
            initPlayerName();
            if (this.otherPlayersBoards.containsValue(this.player))
                JOptionPane.showMessageDialog(null, "Player name already selected.", "Error", JOptionPane.ERROR_MESSAGE);
        } while (this.player.equals("") || this.otherPlayersBoards.containsValue(this.player));
        this.game = new PuzzleBoard(
                this.rows,
                this.columns,
                imagePath,
                myPort,
                tiles,
                selectedTiles,
                this.player,
                this,
                this.otherPlayersBoards.values());
        for (GameService otherGameService : this.otherPlayersBoards.keySet())
            try {
                otherGameService.playerJoined(this);
            } catch (RemoteException e) {
                try {
                    this.playerLeft(otherGameService);
                } catch (RemoteException ignored) { }
            }
        this.game.setVisible(true);
    }

    @Override
    public void playerJoined(GameService gameService) throws RemoteException {
        this.otherPlayersBoards.put(gameService, gameService.getPlayer());
        this.game.addPlayer(gameService.getPlayer());
    }

    @Override
    public void playerLeft(GameService gameService) throws RemoteException {
        this.game.removePlayer(this.otherPlayersBoards.get(gameService));
        this.otherPlayersBoards.remove(gameService);
    }

    @Override
    public Map<GameService, String> getCurrentPlayers() throws RemoteException {
        final Map<GameService, String> players = new HashMap<>(this.otherPlayersBoards);
        players.put(this, this.player);
        return players;
    }

    @Override
    public int getRows() throws RemoteException {
        return this.rows;
    }

    @Override
    public int getColumns() throws RemoteException {
        return this.columns;
    }

    @Override
    public List<Tile> getTiles() throws RemoteException {
        return this.game.getTiles();
    }

    @Override
    public Map<String, Tile> getSelectedTiles() throws RemoteException {
        return this.game.getSelectedTiles();
    }

    @Override
    public String getPlayer() throws RemoteException {
        return this.player;
    }

    @Override
    public void notifyTileSelected(int currentPosition) throws RemoteException {
        this.selectTile(this.player, currentPosition);
        final List<GameService> others = new ArrayList<>(this.otherPlayersBoards.keySet());
        for (GameService gameService : others) {
            try {
                gameService.selectTile(this.player, currentPosition);
            } catch (RemoteException e) {
                try {
                    this.playerLeft(gameService);
                    for (GameService otherGameService : this.otherPlayersBoards.keySet())
                        otherGameService.playerLeft(gameService);
                } catch (RemoteException ignored) { }
            }
        }
    }

    @Override
    public void handleLock(int currentPosition) throws RemoteException {
        new Thread(() -> {
            boolean canLock = true;
            for (GameService gameService : this.otherPlayersBoards.keySet())
                try {
                    canLock = canLock && gameService.canLock(currentPosition);
                } catch (RemoteException e) {
                    try {
                        this.playerLeft(gameService);
                        for (GameService otherGameService : this.otherPlayersBoards.keySet())
                            otherGameService.playerLeft(gameService);
                    } catch (RemoteException ignored) {
                    }
                }
            if (canLock) {
                try {
                    this.notifyTileSelected(currentPosition);
                } catch (RemoteException ignored) { }
            }
            else
                this.game.cancelLock();
        }).start();
    }

    @Override
    public boolean canLock(int currentPosition) throws RemoteException {
        return this.game.canLockTile(this.player, currentPosition);
    }

    @Override
    public void selectTile(String player, int currentPosition) throws RemoteException {
        SwingUtilities.invokeLater(() -> this.game.selectTile(player, currentPosition));
    }

    private void initPlayerName() {
        this.player = JOptionPane.showInputDialog(null, "Insert your desired player name: ", "Info", JOptionPane.INFORMATION_MESSAGE);
        if (this.player == null){
            System.exit(0);
        }
        if (this.player.equals(""))
            JOptionPane.showMessageDialog(null, "Could not select empty player name", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
