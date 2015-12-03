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
import java.util.HashMap;
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
    public static final HashMap<String, Integer> memoize = new HashMap<String, Integer>();
    public static final int WE_WIN = 1000;
    public static final int WE_DRAW = 100;
    public static final int WE_LOSE = -1000;
    public static final int NONE = -999;
    private static final int[] COL_ORDER = { 3, 4, 2, 5, 1, 6, 0 };
    public static BotParser parser;
    private final Random rand = new Random();
    private Field field;

    public void setField(Field field) {
	this.field = field;
    }

    /**
     * Makes a turn. Edit this method to make your bot smarter.
     *
     * @return The column where the turn was made.
     */
    public int makeTurn() {
	final long start = System.currentTimeMillis();
	final int enemyId = 3 - BotParser.mBotId; // 3-2=1; 3-1=2

	int bestVal = WE_LOSE;
	int bestCol = COL_ORDER[0];
	int currentVal;
	for (int i = 0; i < COL_ORDER.length; i++) {
	    if (field.isValidMove(COL_ORDER[i])) {
		currentVal = getColumnValue(field.toString(), COL_ORDER[i], BotParser.mBotId, enemyId, 8);
		if (currentVal > bestVal) { // minimize opponent value
		    bestVal = currentVal;
		    bestCol = COL_ORDER[i];
		    if (bestVal == WE_WIN) {
			break;
		    }
		}
	    }
	}
	final long duration = System.currentTimeMillis() - start;
	System.err.println("Col: " + bestCol + " in " + duration + "ms");

	return bestCol;
    }

    /**
     * Evaluates the value of throwing a coin in a column
     * @param fieldStr current state of the field (before throwing the coin)
     * @param column the column you want to throw in
     * @param player the player you want to get the value for
     * @param opponent the opponent of the player
     * @param branch limiting factor, lower means quicker answer, but less in-depth investigation. Suggested value at least 8
     * @return one of the constants WIN LOSE or DRAW
     */
    private int getColumnValue(final String fieldStr, final int column, final int player, final int opponent, final int branch) {

	if (branch <= 0) { // limit branching for time constraint
	    return WE_LOSE;
	}

	final Field newField = new Field(field.getNrColumns(), field.getNrRows());
	newField.parseFromString(fieldStr);
	newField.addDisc(column, player);
	// System.err.println(localField.toPrettyString());

	// use memorization to quicken the pace
	final String newFieldString = newField.toString();
	final Integer rememberedAnswer = memoize.get(newFieldString);
	if (rememberedAnswer != null) {
	    return rememberedAnswer;
	}

	if (newField.hasFourInARow(player)) {
	    memoize.put(newFieldString, WE_WIN);
	    return WE_WIN;
	} else if (newField.hasFourInARow(opponent)) {
	    memoize.put(newFieldString, WE_LOSE);
	    return WE_LOSE;
	} else if (newField.isFull()) {
	    memoize.put(newFieldString, WE_DRAW);
	    return WE_DRAW;
	}

	int enemyVal = WE_WIN;
	int currentVal;
	for (int i = 0; i < COL_ORDER.length; i++) {
	    if (newField.isValidMove(COL_ORDER[i])) {
		// opponents move
		currentVal = getColumnValue(newFieldString, COL_ORDER[i], opponent, player, branch - 1);
		if (currentVal < enemyVal) { // minimize opponent value
		    enemyVal = currentVal;
		    if (enemyVal == WE_LOSE) {
			break;
		    }
		}
	    }
	}

	if (enemyVal == WE_LOSE) {
	    memoize.put(newFieldString, WE_WIN);
	    return WE_WIN; // opponent loss
	} else if (enemyVal == WE_WIN) {
	    memoize.put(newFieldString, WE_LOSE);
	    return WE_LOSE; // opponent win
	} else {
	    memoize.put(newFieldString, WE_DRAW);
	    return WE_DRAW;
	}
    }


    /**
     * MAIN METHOD
     */
    public static void main(String[] args) {
	// Testing code for new field methods
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
