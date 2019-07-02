package player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Player {

    private static final String WRONG_NAME_INPUT_MESSAGE = "Name is Too short";
    private static final String REGEX_REMOVING_INVALID_SYMBOLS_FROM_CONSOLE = "[^\\w]";
    private static final String REGEX_FOR_CORECT_NUMBER = "^\\d{4}$";

    private String name;
    private PrintWriter playerWriter;
    private BufferedReader playerReader;
    private String playerNumber;

    /**
     * general purpose constructor, initializes member variables /@param name
     * player name, ant playerReader and player Writer
     * 
     * @param clientWriter
     *            player text output
     * @param clientReader
     *            player text input
     */
    public Player(PrintWriter clientWriter, BufferedReader clientReader) {
        if ((clientWriter == null) || (clientReader == null))
            throw new NullPointerException("Player: null data supplied");
        this.setPlayerWriter(clientWriter);
        this.setPlayerReader(clientReader);
    }

    /**
     * Asks every player that joins for his name, validate it by the criteria of
     * minimal of three letters.
     * 
     * @return String name of the player
     */
    public String askForName() {

        write("Please enter your name: ");
        try {
            String inputName = null;
            do {
                inputName = readLineFromPlayer();
                if (inputName != null) {
                    inputName = inputName.trim().replaceAll(REGEX_REMOVING_INVALID_SYMBOLS_FROM_CONSOLE, "");
                    if (inputName.length() < 3) {
                        write(WRONG_NAME_INPUT_MESSAGE);
                        inputName = null;
                    }
                }
            } while (inputName == null);
            return inputName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads a line containing four digits. Validate them and returns them as
     * String. Removes any non letter/digit symbols.
     * 
     * @return String four-digit number with different digits.
     * @throws IOException
     *             if the inputStream is closed or there is IO problem
     */
    public String readFourDigits() throws IOException {

        String result = null;
        do {
            result = playerReader.readLine();
            if (result != null) {
                result = result.replaceAll(REGEX_REMOVING_INVALID_SYMBOLS_FROM_CONSOLE, "");
            }
            if (!isValidInput(result)) {
                result = null;
                write("Invalid or incorrect number.");
            }
        } while (result == null);
        return result;
    }

    /**
     * Check if the input(@param result) is a valid four digit number with
     * different digits.
     * 
     * @param result
     *            line that has been read from player`s console.
     * @return true if the input matches the conditions. False otherwise or if
     *         the input is null.
     */
    private boolean isValidInput(String result) {

        // if the String is not null trim it and check with regular expression
        if (result != null && (result = result.trim()).matches(REGEX_FOR_CORECT_NUMBER)) {
            // Checks for duplicate digits.
            // If there is a digit found more than one time returns false
            if (result.chars().mapToObj(code -> (char) code).distinct().count() == 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * writes a string to the player output. Write
     * 
     * @param stringToBeWrittenToPlayerConsole
     *            the string to write
     */
    public void write(String stringToBeWrittenToPlayerConsole) {

        playerWriter.println(stringToBeWrittenToPlayerConsole);
        playerWriter.flush();
    }

    /**
     * Read line from Player`s input stream.
     * 
     * @return String the line read.
     * @throws IOException
     *             socket closed or some other I/O error.
     */
    public String readLineFromPlayer() throws IOException {

        String line = null;
        do {
            line = playerReader.readLine();
            if (line != null) {
                line = line.replaceAll(REGEX_REMOVING_INVALID_SYMBOLS_FROM_CONSOLE, "");
            }
        } while (line == null);
        return line;

    }

    /**
     * closes player streams
     */
    public void closeStreams() {

        try {
            playerReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerWriter.close();
    }

    public Player(String playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PrintWriter getPlayerWriter() {
        return playerWriter;
    }

    public void setPlayerWriter(PrintWriter playerWriter) {
        this.playerWriter = playerWriter;
    }

    public BufferedReader getPlayerReader() {
        return playerReader;
    }

    public void setPlayerReader(BufferedReader playerReader) {
        this.playerReader = playerReader;
    }

    public String getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(String playerNumber) {
        this.playerNumber = playerNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((playerReader == null) ? 0 : playerReader.hashCode());
        result = prime * result + ((playerWriter == null) ? 0 : playerWriter.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Player other = (Player) obj;
        if (playerReader == null) {
            if (other.playerReader != null)
                return false;
        } else if (!playerReader.equals(other.playerReader))
            return false;
        if (playerWriter == null) {
            if (other.playerWriter != null)
                return false;
        } else if (!playerWriter.equals(other.playerWriter))
            return false;
        return true;
    }
}