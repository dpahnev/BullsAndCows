package thread;

import java.io.IOException;

import exception.InvalidPlayerException;
import game.BullCowGame;
import player.Player;
import player.PlayersHolder;

/**
 * this class handles new user. its task is simple: to ask if the user wants to
 * play or to wait to be asked for a game.
 */
public class PlayerWrapperThread implements Runnable {

//@formatter:off
    private static final String PLAYER_POSSIBLE_GAME_OPTIONS_MESSAGE = "Hello:\n"
                                                      + "Type 0: for a game with random player.\n"
                                                      + "Type 1: for a room to pick a friend to play with.";
//@formatter:on
    Player player;
    PlayersHolder holder;

    /**
     * general purpose constructor. Receives parameters existing player and
     * PlayersHolder object. Initialize the class fields with them.
     * 
     * @param newPlayer
     *            the new player
     */
    public PlayerWrapperThread(Player newPlayer, PlayersHolder holder) {
        this.player = newPlayer;
        this.holder = holder;
    }

    /**
     * General logic for New Player. Wraps the player in Thread so he can choose
     * game mode independent.
     */
    public void run() {

        player.setName(player.askForName());
        try {
            int option = chooseGameOption();

            if (option == 0) {
                randomOpponentOption();
                // The thread has done it`s job.
                return;
            }
            if (option == 1) {
                playWithFriendsOption();
            }
        } catch (IOException e) {
            System.err.println(player.getName() + " has disconected.");
        }
    }

    /**
     * Let the player choose game option.
     * 
     * @return Integer representing the option
     * @throws IOException
     *             if there is IO problem with the player
     */
    private int chooseGameOption() throws IOException {
        
        displayPossibleOptions();
        int option = getPlayerOption();
        System.out.println(player.getName() + " chose option: " + option);
        return option;
    }

    /**
     * Option for random opponent. Joins the player in the RandomPlayersRoom,
     * see{@link PlayersHolder#addPlayerToRandomRoom(Player)}}
     * 
     * 
     */
    private void randomOpponentOption() {

        holder.addPlayerToRandomRoom(player);
        player.write("you chose to play game with random opponent.");
        System.out.println(player.getName() + " chose : option RandomPlayerRoom");
    }

    /**
     * Option for playing with friends. Has additional options. To invite friend
     * or wait to be invited.
     * 
     * @throws IOException
     *             If the connection to the current player is lost.
     */
    private void playWithFriendsOption() throws IOException {

        int choice;
        while (!Thread.interrupted()) {
            String freePlayers = holder.printFreePlayersFromFriendRoom();
            displayPossibleChoices(freePlayers);
            choice = getPlayerChoice();
            // choice to be invited or to wait for players to join

            if (choice == 0) {
                holder.addplayer(player);
                player.write("you chose to wait for friend as opponent.");
                // The thread has done it`s job.
                return;
            } else {
                System.out.println(player.getName() + " chose to play with number : " + choice);
                if (askPlayerForGame(choice)) {
                    startNewGame(choice);
                    return;
                }
                player.write("The other player refused to play");
            }
        }
    }

    /**
     * method that starts the game. Prepares players and finalize the threads of
     * {@link PlayerWrapperThread}.
     * 
     * @param choice
     *            the choise of the player
     */
    private void startNewGame(int choice) {

        BullCowGame newGame;
        Player opponent = null;
        try {
            opponent = holder.getPlayerByIndex(choice);
        } catch (InvalidPlayerException e1) {
            System.out.println("invalid index." + choice + " This player does not exist.");
            e1.printStackTrace();
        }

        try {
            holder.removePlayerByIndex(choice);
        } catch (InvalidPlayerException e) {
            System.out.println("unexpected failure. Trying to remove player with wrong index.");
            e.printStackTrace();
        }
        newGame = new BullCowGame(opponent, player, holder);
        new Thread(newGame).start();
        System.out.println("Game starting. " + player.getName() + " vs " + opponent.getName());
    }

    /**
     * Ask specific player does he wants to play this game with you.
     * 
     * @param choice
     *            index of player in friends room
     * @param freePlayers
     *            friends`s room
     * @throws IOException
     *             if there is problem with one of the players.
     */
    public boolean askPlayerForGame(int choice) throws IOException {

        Player opponent;
        try {
            opponent = holder.getPlayerByIndex(choice);
        } catch (InvalidPlayerException e) {
            this.player.getPlayerWriter().println("Invalid index. Or player already in game.");
            return false;
        }
        if (this.equals(opponent)) {
            System.out.println("He is trying to play with himself.");
            return false;
        }
        opponent.write("");
        player.write("");
        // strange, but game title is p2 vs p1
        return getAnswerFromOpponent(opponent, choice);

    }

    private boolean getAnswerFromOpponent(Player opponent, int choice) throws IOException {

        opponent.write(
                "Do you want to play with " + player.getName() + " with index " + choice + "yes or no!");
        System.out.println("question asked does " + opponent.getName() + " wants to play with " + player.getName());

        String responseFromOpponent = null;
        do {
            responseFromOpponent = opponent.readLineFromPlayer().toLowerCase();
            if (!"no".equals(responseFromOpponent) && !"yes".equals(responseFromOpponent)) {
                responseFromOpponent = null;
                System.out.println("Wrong Answer from " + opponent.getName() + " : " + responseFromOpponent);
            }
        } while (responseFromOpponent == null);
        if ("yes".equals(responseFromOpponent)) {
            System.err.println("after do/while response is " + responseFromOpponent);
            return true;
        }

        return false;
    }

    /**
     * Display on the player`s console the options of the game.
     */
    public void displayPossibleOptions() {
        player.write(PLAYER_POSSIBLE_GAME_OPTIONS_MESSAGE);
    }

    private int getPlayerOption() throws IOException {

        int choice;
        do {
            try {
                choice = Integer.parseInt(player.readLineFromPlayer());
            } catch (NumberFormatException nfx) {
                choice = -1;
            }
            // TODO to add interface for options
        } while (choice != 0 && choice != 1);
        return choice;
    }

    /**
     * displays the possible choices to the default output
     */
    public void displayPossibleChoices(String freePlayers) {

        player.write("");
        player.write("Free Players:");
        player.write("0) (wait for another player to choose you)");
        player.write(freePlayers);
        player.write("Choose player: ");
    }

    /**
     * Get the player chosen number representing the player index he wants to
     * play.
     * 
     * @return integer representing the player index
     * @throws IOException
     *             if error in reading/writing
     */
    private int getPlayerChoice() throws IOException {

        int choice;
        do {
            try {
                choice = Integer.parseInt(player.readLineFromPlayer());
            } catch (NumberFormatException nfx) {
                choice = -1;
            }
        } while (choice <= -1);
        return choice;
    }

}