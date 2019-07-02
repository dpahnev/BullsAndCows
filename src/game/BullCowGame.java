package game;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import player.Player;
import player.PlayersHolder;
import thread.PlayerWrapperThread;

/**
 * This class represents a game of bulls and cows.
 */
public class BullCowGame implements Runnable {

    private static final int END_GAME_MESSAGE_TIME_TO_SEE = 10000;

    private Player player1;// the player that starts first
    private Player player2;// the player that is second
    AtomicBoolean isOver = new AtomicBoolean(false);

    /**
     * the holder, because when the game ends, holder must be given to
     * NewPlayerThread
     */
    private PlayersHolder holder;

    /**
     * general purpose constructor, initializes data. Two players and a holder
     * to return the players after the game has ended.
     * 
     * @param player1
     *            the player that was asked to play
     * @param player2
     *            the player that started the game
     * @param holder
     *            the holder , because when the game ends, player must be given
     *            to NewPlayerThread
     */
    public BullCowGame(Player player1, Player player2, PlayersHolder holder) {
        this.player1 = player1;
        this.player2 = player2;
        this.holder = holder;
    }

    /**
     * The start of the game. Asks each player about his starting number and run
     * the game. see @StartGame().
     *
     */
    public void run() {
        startGameMessage();
        try {
            readStartingNumber(player1);
            readStartingNumber(player2);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
        try {
            startGame();
        } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Method for reading the asked player number with which he will play this
     * game.
     * 
     * @throws IOException
     *             if problem with IO of the player.
     */
    public void readStartingNumber(Player currentPlayer) throws IOException {

        currentPlayer.write("Please insert your starting number: ");
        currentPlayer.setPlayerNumber(currentPlayer.readFourDigits());
    }

    /**
     * The game method. Base logic of the game here.
     * 
     * @throws IOException
     */
    public void startGame() throws IOException {
        Player playerOnTurn = player1;
        Player playerOffTurn = player2;
        while (!isOver.get()) {
            // asking player is first
            String playerOnTurnGuess = getPlayerGuess(playerOnTurn);
            String playerOffTurnNumber = playerOffTurn.getPlayerNumber();
            int playerOnTurnCows = calculateCows(playerOnTurnGuess, playerOffTurnNumber);
            int playerOnTurnBulls = calculateBulls(playerOnTurnGuess, playerOffTurnNumber);

            System.out.println(
                    playerOnTurn.getName() + "guessed " + playerOnTurnCows + " cows " + playerOnTurnBulls + " bulls.");
            String guessString = " --> " + playerOnTurnCows + " cows, " + playerOnTurnBulls + " bulls.";

            playerOnTurn.write(guessString);
            playerOffTurn.write("\t" + playerOnTurn.getName() + "'s Guess: "
                    + playerOnTurnGuess + System.lineSeparator() + guessString);

            if (checkForWinner(playerOnTurnBulls)) {
                endGameMessage(playerOnTurn, playerOffTurn);
                isOver.set(true);
            } else {
                Player temp = playerOnTurn;
                playerOnTurn = playerOffTurn;
                playerOffTurn = temp;
            }
        }
        try {
            Thread.sleep(END_GAME_MESSAGE_TIME_TO_SEE);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        // TODO new Thread
        askPlayersForNewGame(player1);
        askPlayersForNewGame(player2);
    }

    /**
     * Gets the player guessed digits and returns them as String.
     * 
     * @param currentPlayerOnTurn
     *            the player that has to make a move.
     * @throws IOException
     *             if there is IO problem with the player.
     */
    public String getPlayerGuess(Player currentPlayerOnTurn) throws IOException {

        currentPlayerOnTurn.write("Please make your guess: ");
        return currentPlayerOnTurn.readFourDigits();
    }

    /**
     * counts the number of bulls for the current guess of the player.
     * 
     * @param number
     *            the number of the player, with which he plays the game.
     * @param guess
     *            the other player`s guess number.
     * @return cows return integer how many cows the player who guessed has.
     */
    private int calculateBulls(String number, String guess) {

        int result = 0;
        for (int i = 0; i < number.length(); i++) {
            for (int j = 0; j < guess.length(); j++) {
                if (guess.charAt(j) == number.charAt(i) && i == j) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * counts the number of cows for the current guess of the player.
     * 
     * @param number
     *            the number of the player, with which he plays the game.
     * @param guess
     *            the other player`s guess number.
     * @return cows return integer how many cows the player who guessed has.
     */
    private int calculateCows(String number, String guess) {

        int result = 0;
        for (int i = 0; i < number.length(); i++) {
            for (int j = 0; j < guess.length(); j++) {
                if (guess.charAt(j) == number.charAt(i) && i != j) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * Checks if the game has reached the condition for end.
     * 
     * @param currentPlayerBulls
     *            bulls of the current player
     * @return returns boolean if the condition for winning the game is
     *         reached(does the current player wins).
     */
    private boolean checkForWinner(int currentPlayerBulls) {

        if (currentPlayerBulls == 4) {
            isOver.set(true);
        }
        return isOver.get();
    }

    /**
     * Prints the message that is printed to both players at the start of the
     * game.
     */
    private void startGameMessage() {

        String gameTitle = player1.getName() + " vs " + player2.getName() + " Game started.";
        player1.write(gameTitle);
        player2.write(gameTitle);
    }

    /**
     * Prints the message that is printed to both players at the end of the
     * game. Including who wins and who loses.
     */
    private void endGameMessage(Player winner, Player loser) {

        winner.write("Game end. You Win!");
        loser.write("Game end. You lost!. Winner - " + player1.getName());
    }

    private void askPlayersForNewGame(Player currentlyAskedPlayer) {

        currentlyAskedPlayer.write("Do you want to play more.");
        String answer = null;
        do {
            try {
                answer = currentlyAskedPlayer.readLineFromPlayer().toLowerCase();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!"no".equals(answer) && !"yes".equals(answer)) {
                answer = null;
                System.out.println("Wrong Answer. yes or no !");
            }
        } while (answer == null);
        if ("yes".equals(answer)) {
            new Thread(new PlayerWrapperThread(currentlyAskedPlayer, holder)).start();
            // TODO start new thread to ask them so their choice to be
            // Independent(lambda).
        } else {
            currentlyAskedPlayer.closeStreams();
        }
    }

}