package player;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import exception.InvalidPlayerException;
import game.BullCowGame;

/**
 * this inner class is used as a closure(implements Holder), because we do not
 * want to give reference to the whole BCServer class. It holds the
 * waitingClients and adds or gives their list it is made as a thread so the
 * BCServer does not have to wait this class's operations
 */
public class PlayersHolder implements Runnable {

    private Queue<Player> randomRoomPlayers;
    /**
     * Class that encapsulate different room of players.
     */
    private FreePlayers freePlayersRoom;

    /**
     * default constructor. Initializes the collection and the new room.
     */
    public PlayersHolder() {
        randomRoomPlayers = new LinkedBlockingQueue<>();
        freePlayersRoom = new FreePlayers();
    }

    /**
     * initializes data starts the thread. The purpose of the thread is when
     * awakened by notify() to check, if there are enough players to start game
     * and initiate game until there are no more players and wait.
     */
    public void run() {

        while (!Thread.interrupted()) {
            while (randomRoomPlayers.size() > 1) {
                Player firstPlayer = randomRoomPlayers.poll();
                Player secondPlayer = randomRoomPlayers.poll();
                Thread newGameThread = new Thread(new BullCowGame(firstPlayer, secondPlayer, this));
                newGameThread.start();
                System.out.println("Game initiated by PlayersHolder.");
            }
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * Add player to the playersHolder. Does it synchronizing on the players
     * linkedBlockingQueue is concurrent.
     * 
     * @param player
     *            The player to be added to waiting players
     */
    public void addPlayerToRandomRoom(Player player) {

        randomRoomPlayers.add(player);
        synchronized (this) {
            notify();
        }
    }

    /**
     * Delegate method to freeplayersRoom. Calls
     * {@link FreePlayers#addPlayerToFriendRoom(Player)}
     * 
     * @param player
     *            current player to be added.
     */
    public void addplayer(Player player) {
        freePlayersRoom.addPlayerToFriendRoom(player);
    }

    /**
     * Delegate method of FreePlayers class see
     * {@link FreePlayers#printFreePlayersIndexes()}.
     * 
     * @return String information about the players
     */
    public String printFreePlayersFromFriendRoom() {
        return freePlayersRoom.printFreePlayersIndexes();
    }

    /**
     * Delegate method of {@link FreePlayers#getPlayer(int)}
     * 
     * @param indexOfFreePlayer
     *            the player index
     * @return the player with this index
     * @throws InvalidPlayerException
     *             if the index given as parameter does not exist in
     *             FreePlayers.
     * 
     */
    public Player getPlayerByIndex(int indexOfFreePlayer) throws InvalidPlayerException {
        return freePlayersRoom.getPlayer(indexOfFreePlayer);
    }

    /**
     * Delegate method. see{@link FreePlayers#removePlayer(int)}
     * 
     * @param indexOfPlayerToBeRemoved
     *            index of the player that needs to be removed.
     * @throws InvalidPlayerException
     *             if the index is not valid or there is no player with such
     *             index.
     */
    public void removePlayerByIndex(int indexOfPlayerToBeRemoved) throws InvalidPlayerException {
        freePlayersRoom.removePlayer(indexOfPlayerToBeRemoved);
    }

}