package puzzle.rmi.services;

import puzzle.rmi.PuzzleBoard;
import puzzle.rmi.Tile;
import puzzle.rmi.services.interfaces.GameService;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

//TODO Per tracciare un giocatore crashato, rimuoverlo nel catch del RemoteException
public class GameServiceImpl implements GameService {

    private int rows;
    private int columns;
    private transient PuzzleBoard game;
    private final Map<GameService, String> otherPlayersBoards = new HashMap<>();
    private String player;

    @Override
    public void startNewGame(String imagePath, int rows, int columns, int myPort) throws RemoteException {
        do {
            this.player = JOptionPane.showInputDialog(null, "Insert your desired player name: ", "Info", JOptionPane.INFORMATION_MESSAGE);
            if (this.player == null){
                System.exit(0);
                break;
            }
            if (this.player.equals(""))
                JOptionPane.showMessageDialog(null, "Could not select empty player name", "Error", JOptionPane.ERROR_MESSAGE);
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
        do {
            this.player = JOptionPane.showInputDialog(null, "Insert your desired player name: ", "Info", JOptionPane.INFORMATION_MESSAGE);
            if (this.player == null){
                System.exit(0);
                break;
            }
            if (this.player.equals(""))
                JOptionPane.showMessageDialog(null, "Could not select empty player name", "Error", JOptionPane.ERROR_MESSAGE);
        } while (this.player.equals(""));
        Registry registry = LocateRegistry.getRegistry(host, joinPort);
        try {
            GameService gameService = (GameService) registry.lookup(String.valueOf(joinPort));
            this.rows = gameService.getRows();
            this.columns = gameService.getColumns();
            this.game = new PuzzleBoard(
                    this.rows,
                    this.columns,
                    imagePath,
                    myPort,
                    gameService.getTiles(),
                    gameService.getSelectedTiles(),
                    player,
                    this);reg
            this.playerJoined(gameService);
            this.otherPlayersBoards.addAll(gameService.getCurrentPlayers());
            gameService.notifyPlayerJoined(this);
            this.game.setVisible(true);
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyPlayerJoined(GameService gameService) throws RemoteException {
        for (GameService otherGameService : this.otherPlayersBoards)
            otherGameService.playerJoined(gameService);
        this.playerJoined(gameService);
    }

    @Override
    public void playerJoined(GameService gameService) throws RemoteException {
        this.otherPlayersBoards.add(gameService);
        this.game.addPlayer(gameService.getPlayer());
    }

    @Override
    public void notifyPlayerLeft(GameService gameService) throws RemoteException {
        this.playerLeft(gameService);
        for (GameService otherGameService : this.otherPlayersBoards)
            otherGameService.notifyPlayerLeft(gameService);
    }

    @Override
    public void playerLeft(GameService gameService) throws RemoteException {
        this.otherPlayersBoards.remove(gameService);
        this.game.removePlayer(gameService.getPlayer());
    }

    @Override
    public List<GameService> getCurrentPlayers() throws RemoteException {
        return this.otherPlayersBoards;
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
    public String getPlayer() {
        return this.player;
    }

    @Override
    public void notifyTileSelected(int currentPosition) throws RemoteException {
        this.selectTile(this.player, currentPosition);
        for (GameService gameService: this.otherPlayersBoards)
            try {
                gameService.selectTile(this.player, currentPosition);
            } catch (RemoteException e) {
                this.notifyPlayerLeft(gameService);
            }
    }

    @Override
    public void selectTile(String player, int currentPosition) throws RemoteException {
        this.game.selectTile(player, currentPosition);
    }
}
