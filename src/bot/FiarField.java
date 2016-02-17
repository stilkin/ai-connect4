package bot;

import java.util.Arrays;

public class FiarField {
    public static final int COLS = 7;
    public static final int ROWS = 6;
    private final int[][] cells = new int[COLS][ROWS];
    private final int[] playerCoins = new int[5];
    private int totalCoins = 0;
    private int lastCol = -1;
    private int lastRow = -1;
    private int lastPlayer = -1;

    /**
     * Copy method
     * 
     * @param field
     */
    public void init(final int[][] field) {
	totalCoins = 0;
	for (int x = 0; x < COLS; x++) {
	    for (int y = 0; y < ROWS; y++) {
		cells[x][y] = field[x][y];
		if(cells[x][y] != 0) {
		    totalCoins++;
		}
	    }
	}
    }

    /**
     * Add disc to column for player
     * 
     * @param column
     * @param player
     * @return
     */
    public boolean addDisc(final int column, final int player) {
	for (int y = ROWS - 1; y >= 0; y--) { // from bottom column up ??
	    if (cells[column][y] == 0) {
		cells[column][y] = player;
		lastCol = column;
		lastRow = y;
		lastPlayer = player;
		totalCoins++;
		playerCoins[player]++;
		return true;
	    }
	}
	return false;
    }

    /**
     * Checks if column is full
     * 
     * @param column
     * @return
     */
    public boolean isValidMove(final int column) {
	return (cells[column][0] == 0);
    }

    /**
     * Checks if the last move has won the game
     */
    public boolean hasWon() {
	if (playerCoins[lastPlayer] < 4) { // not enough coins for 4iar
	    return false;
	}

	if (hasFourHorizontal()) {
	    return true;
	}

	if (hasFourVertical()) {
	    return true;
	}

	if (hasFourDiagonal()) {
	    return true;
	}

	return false;
    }

    private boolean hasFourVertical() {
	int count = 0;
	for (int y = 0; y < ROWS; y++) {
	    if (cells[lastCol][y] == lastPlayer) {
		count++;
		if (count >= 4) {
		    return true;
		}
	    } else {
		count = 0;
	    }
	}
	return false;
    }

    private boolean hasFourHorizontal() {
	int count = 0;
	for (int x = 0; x < COLS; x++) {
	    if (cells[x][lastRow] == lastPlayer) {
		count++;
		if (count >= 4) {
		    return true;
		}
	    } else {
		count = 0;
	    }
	}
	return false;
    }

    private boolean hasFourDiagonal() {
	int x, y;
	x = lastCol;
	y = lastRow;

	while (x > 0 && y > 0) { // move to NW
	    x--;
	    y--;
	}

	if (checkNWSEDiagonal(lastPlayer, x, y, 4))
	    return true;

	x = lastCol;
	y = lastRow;
	while (x < (COLS - 1) && y > 0) { // move to NW
	    x++;
	    y--;
	}

	if (checkNESWDiagonal(lastPlayer, x, y, 4))
	    return true;

	return false;
    }

    private boolean checkNWSEDiagonal(final int player, final int i, final int j, final int n) {
	int count = 0;
	for (int x = i, y = j; x < COLS && y < ROWS; x++, y++) {
	    if (cells[x][y] == player) {
		count++;
		if (count >= n) {
		    return true;
		}
	    } else {
		count = 0;
	    }
	}
	return false;
    }

    private boolean checkNESWDiagonal(final int player, final int i, final int j, final int n) {
	int count = 0;
	for (int x = i, y = j; x >= 0 && y < ROWS; x--, y++) {
	    if (cells[x][y] == player) {
		count++;
		if (count >= n) {
		    return true;
		}
	    } else {
		count = 0;
	    }
	}
	return false;
    }

    /**
     * Reset this instance, if you want to recycle the object
     */
    public void reset() {
	for (int x = 0; x < COLS; x++) {
	    for (int y = 0; y < ROWS; y++) {
		cells[x][y] = 0;
	    }
	}
	Arrays.fill(playerCoins, 0);
	lastCol = -1;
	lastRow = -1;
	lastPlayer = -1;
	totalCoins = 0;
    }

    public boolean isFull() {
	return (totalCoins == ROWS * COLS);
    }

    public int[][] getCells() {
	return cells;
    }

    @Override
    public String toString() {
	String prettyStr = "|0|1|2|3|4|5|6|\n";
	for (int y = 0; y < ROWS; y++) {
	    for (int x = 0; x < COLS; x++) {
		prettyStr += " " + cells[x][y];
	    }
	    prettyStr += '\n';
	}
	return prettyStr;
    }

}
