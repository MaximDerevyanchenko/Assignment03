package puzzle.rmi.services;

import puzzle.rmi.Tile;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface GameService extends Remote {

    void startNewGame(String imagePath, int rows, int columns, int myPort) throws RemoteException;

    void joinGame(String imagePath, String host, int joinPort, int myPort) throws RemoteException;

    void playerJoined(GameService gameService) throws RemoteException;

    void playerLeft(GameService gameService) throws RemoteException;

    Map<GameService, String> getCurrentPlayers() throws RemoteException;

    int getRows() throws RemoteException;

    int getColumns() throws RemoteException;

    List<Tile> getTiles() throws RemoteException;

    Map<String, Tile> getSelectedTiles() throws RemoteException;

    String getPlayer() throws RemoteException;

    void notifyTileSelected(int currentPosition) throws RemoteException;

    void selectTile(String player, int currentPosition) throws RemoteException;
}
