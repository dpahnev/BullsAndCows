package player;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import exception.InvalidPlayerException;

/**
 * Class that holds the players that wants to play with their friends. Each
 * player has unique index.
 * 
 */
public class FreePlayers {

    private AtomicInteger lastPlayerIndex = new AtomicInteger(1);
    private Map<Integer, Player> freePlayers;

    /**
     * default constructor to initialize the collection.
     */
    public FreePlayers() {
        freePlayers = new ConcurrentHashMap<>();
    }

    /**
     * Construct String containing info for all players currently waiting for a
     * game.
     * 
     * @return String info for the players.
     */
    public String printFreePlayersIndexes() {

        StringBuffer sbf = new StringBuffer();
        // TODO synchronization
        for (Entry<Integer, Player> currentPlayerEntry : freePlayers.entrySet()) {
            sbf.append(currentPlayerEntry.getKey() + " : for " + currentPlayerEntry.getValue().getName());
            sbf.append(System.lineSeparator());
        }
        return sbf.toString();
    }

    /**
     * By the given player add him to the collection of the free players, if he
     * is not in it already. The player receives unique index with which he will
     * be stored and picked for a game.
     * 
     * @param currentPlayerToBeAdded
     *            player who wants to join the room.
     */
    public void addPlayerToFriendRoom(Player currentPlayerToBeAdded) {

        int currentAvailableIndex;
        currentAvailableIndex = lastPlayerIndex.getAndIncrement();
        freePlayers.put(currentAvailableIndex, currentPlayerToBeAdded);
        System.out.println(freePlayers.get(currentAvailableIndex).getName());
    }

    /**
     * By the given index check does this index exist as key. Returns the player
     * with this index. Otherwise throws {@link InvalidPlayerException}
     * 
     * @param indexOfPlayer
     *            index of the requested player.
     * @return
     * @throws InvalidPlayerException
     *             if there is no player with such index.
     */
    public Player getPlayer(int indexOfPlayer) throws InvalidPlayerException {

        if (freePlayers.containsKey(indexOfPlayer)) {
            return freePlayers.get(indexOfPlayer);
        }
        throw new InvalidPlayerException();
    }

    /**
     * Remove the Player with the current Index, if exist. Otherwise throws
     * {@link InvalidPlayerException}.
     * 
     * @param indexOfplayerToBeRemoved
     *            index of the player whom we want to remove.
     * @throws InvalidPlayerException
     *             if there is no such index.
     */
    public void removePlayer(int indexOfplayerToBeRemoved) throws InvalidPlayerException {

        if (freePlayers.containsKey(indexOfplayerToBeRemoved)) {
            freePlayers.remove(indexOfplayerToBeRemoved);
        }
        throw new InvalidPlayerException();
    }
}
