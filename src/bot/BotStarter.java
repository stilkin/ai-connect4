// // Copyright 2015 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package bot;

import java.util.Random;

/**
 * BotStarter class
 * 
 * Monte Carlo simulation of 4 in a row. Tip of the hat to PrimaBot!
 * 
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 * @author Servaas Tilkin ( edits and patches)
 */

public class BotStarter {
    private static BotParser parser;
    public final Random rand = new Random();
    public final Rating[] colRatings = new Rating[FiarField.COLS];
    public final int MAX_BRANCH = 42; // 42 = no limit
    public final long MAX_TIME = 600; // in ms, 500 is what you get per round
    private Field field;

    public BotStarter() {
	for (int x = 0; x < colRatings.length; x++) {
	    colRatings[x] = new Rating();
	}
    }

    public void setField(Field field) {
	this.field = field;
    }

    /**
     * Makes a turn. Edit this method to make your bot smarter.
     *
     * @return The column where the turn was made.
     */
    public int makeTurn() {
	System.err.println("Round: " + BotParser.round);
	final FiarField fiarField = new FiarField();

	// copy to our own data structure
	for (int y = FiarField.ROWS - 1; y >= 0; y--) {
	    for (int x = 0; x < FiarField.COLS; x++) {
		int disc = field.getDisc(x, y);
		if (disc != 0) {
		    fiarField.addDisc(x, disc);
		}
	    }
	}
	System.err.println(fiarField);

	for (int x = 0; x < FiarField.COLS; x++) {
	    colRatings[x].reset();
	}

	int result;
	FiarField tmpField = new FiarField();
	final long start = System.currentTimeMillis();
	long timeSpent = 0;
	long count = 0;

	while (timeSpent < MAX_TIME) {
	    for (int x = 0; x < FiarField.COLS; x++) {
		tmpField.init(fiarField.getCells());
		if (fiarField.isValidMove(x)) { // only consider valid moves
		    tmpField.addDisc(x, BotParser.myBotId);
		    result = playGame(tmpField, BotParser.myBotId, MAX_BRANCH);
		    if (result == BotParser.myBotId) {
			colRatings[x].wins++;
		    } else if (result == -1) {
			colRatings[x].draws++;
		    } else {
			colRatings[x].losses++;
		    }
		}
	    }
	    count++;
	    timeSpent = System.currentTimeMillis() - start;
	}

	System.err.println("Played " + count + " times in " + timeSpent + "ms");
	int bestCol = 0;
	float bestVal = Integer.MIN_VALUE;
	for (int x = 0; x < FiarField.COLS; x++) {
	    if (fiarField.isValidMove(x)) { // only consider valid moves
		System.err.println(x + " \t" + colRatings[x].getValue() + " \t" + colRatings[x].toString());
		if (colRatings[x].getValue() > bestVal) {
		    bestVal = colRatings[x].getValue();
		    bestCol = x;
		}
	    }
	}

	System.err.println("Decided to go for col " + bestCol);
	return bestCol;
    }

    private int playGame(final FiarField field, final int player, final int barrier) {
	int currentPlayer = player;
	if (field.hasWon()) {
	    return currentPlayer;
	}

	int r;
	int depth = 0;
	while (!field.isFull() && depth < barrier) {
	    currentPlayer = 3 - currentPlayer; // switch players
	    r = rand.nextInt(FiarField.COLS); // choose random column
	    while (!field.isValidMove(r)) {
		r = rand.nextInt(FiarField.COLS);
	    }
	    field.addDisc(r, currentPlayer);
	    if (field.hasWon()) { // check winning
		return currentPlayer;
	    }
	    depth++;
	}
	return -1; // draw game, or depth reached
    }

    /**
     * MAIN METHOD
     */
    public static void main(String[] args) {
	// Testing code for new field methods
	// final FiarField f = new FiarField();
	//
	// f.addDisc(1, 1);
	//
	// f.addDisc(2, 1);
	// f.addDisc(2, 1);
	//
	// f.addDisc(3, 1);
	// f.addDisc(3, 1);
	// f.addDisc(3, 1);
	//
	// for (int i = 0; i < 5; i++) {
	// f.addDisc(i, 2);
	// System.out.println(f.toString());
	// System.out.println(f.hasWon());
	// }

	parser = new BotParser(new BotStarter());
	parser.run();
    }

}
