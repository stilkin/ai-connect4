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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * BotStarter class
 * 
 * Magic happens here. You should edit this file, or more specifically the makeTurn() method to make your bot do more than random moves.
 * 
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 * @author Servaas Tilkin ( edits and patches)
 */

public class BotStarter {
    public static final int NONE = -999;
    public static BotParser parser;
    private Random rand;
    private Field field;

    public void setField(Field field) {
	this.field = field;
	rand = new Random();
    }

    /**
     * Makes a turn. Edit this method to make your bot smarter.
     *
     * @return The column where the turn was made.
     */
    public int makeTurn() {
	final int enemyId = 3 - BotParser.mBotId; // 3-2=1; 3-1=2

	// see if we can win
	for (int col = 0; col < field.getNrColumns(); col++) {
	    if (field.isValidMove(col)) {
		field.addDisc(col, BotParser.mBotId);
		final boolean weHave4 = field.hasFourInARow(BotParser.mBotId);
		field.removeDisc(col);
		if (weHave4) { // winning throw!
		    System.err.println("Going for winning throw in col " + col);
		    return col; // win this!
		}
	    }
	}

	// see if the enemy can win in the next move
	for (int col = 0; col < field.getNrColumns(); col++) {
	    if (field.isValidMove(col)) {
		field.addDisc(col, enemyId);
		final boolean enemyHas4 = field.hasFourInARow(enemyId);
		field.removeDisc(col);
		if (enemyHas4) { // enemy can get four in a row!
		    System.err.println("Blocking enemy win in col " + col);
		    return col; // block this!
		}
	    }
	}

	// see if the enemy can win on top of our coin
	final List<Integer> allowedCols = new ArrayList<Integer>();
	for (int col = 0; col < field.getNrColumns(); col++) {
	    if (field.isValidMove(col)) {
		field.addDisc(col, BotParser.mBotId);
		if (field.isValidMove(col)) {
		    field.addDisc(col, enemyId);
		    final boolean enemyHas4 = field.hasFourInARow(enemyId);
		    if (!enemyHas4) {
			allowedCols.add(col);
		    }
		    field.removeDisc(col);
		}
		field.removeDisc(col);
	    }
	} // TODO: expand to fill field? or will this take too long?

	// if (allowedCols.size() > 0) {
	// // return a random move of the allowed cols
	// final int randCol = rand.nextInt(allowedCols.size());
	// return allowedCols.get(randCol);
	// }

	// TODO: this is rubbish, change default behavior!!
	return defaultBehavior(allowedCols);
    }

    /**
     * Default behavior is to throw as close to the middle as possible
     * 
     * @return The column where the coin should be dropped.
     */
    private int defaultBehavior(final List<Integer> allowedCols) {
	final int dimension = this.field.getNrColumns(); // get field width
	final int favoriteColumn = (int) (dimension / 2); // yay for the middle column

	int tryCol;
	for (int i = 0; i < favoriteColumn + 1; i++) {
	    tryCol = favoriteColumn + i; // walk to the right
	    if (allowedCols.contains(tryCol)) { // valid move because it is in the list
		System.err.println("Default throw to the right: " + tryCol);
		return tryCol;
	    }

	    tryCol = favoriteColumn - i; // walk to the left
	    if (allowedCols.contains(tryCol)) { // valid move because it is in the list
		System.err.println("Default throw to the left: " + tryCol);
		return tryCol;
	    }

	}

	// if all else fails (which shouldn't happen)
	for (int col = 0; col < field.getNrColumns(); col++) {
	    if (this.field.isValidMove(col)) {
		System.err.println("Error throw " + col);
		return col;
	    }
	}
	System.err.println("Serious problems " + Integer.MIN_VALUE);
	return Integer.MIN_VALUE; // because we can *cough*
    }

    /**
     * MAIN METHOD
     */
    public static void main(String[] args) {
	// final Field f = new Field(7, 6);
	//
	// f.addDisc(1, 1);
	// f.addDisc(1, 1);
	// f.addDisc(1, 1);
	// f.addDisc(2, 1);
	// f.addDisc(2, 1);
	// f.addDisc(3, 1);
	// f.addDisc(4, 1);
	//
	// for (int i = 0; i < 5; i++) {
	// f.addDisc(i, 2);
	// }
	//
	// System.out.println("Player 1 has 4iar: " + f.hasFourInARow(1));
	// System.out.println("Player 2 has 4iar: " + f.hasFourInARow(2));
	// System.out.println(f.toString());

	parser = new BotParser(new BotStarter());
	parser.run();
    }

}
