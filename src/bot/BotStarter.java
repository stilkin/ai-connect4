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

import java.util.Arrays;
import java.util.HashMap;

/**
 * BotStarter class
 * 
 * Magic happens here. You should edit this file, or more specifically the makeTurn() method to make your bot do more than random moves.
 * 
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 * @author Servaas Tilkin ( edits and patches)
 */

public class BotStarter {
    public static final HashMap<String, Integer> gameMemory = new HashMap<String, Integer>();
    public static final HashMap<String, Integer> roundMemory = new HashMap<String, Integer>();

    public static final int MAX_BRANCH = 6; // keep under 8 to prevent timeouts
    public static final int WINNING = 10;
    public static final int WE_DRAW = 0;
    public static final int LOSING = -10;
    private static final int[] COL_ORDER = { 3, 4, 2, 5, 1, 6, 0 }; // { 0, 6, 1, 5, 2, 4, 3 };
    public static BotParser parser;
    private Field field;
    private long roundStart;

    public void setField(Field field) {
	this.field = field;
    }

    /**
     * Makes a turn. Edit this method to make your bot smarter.
     *
     * @return The column where the turn was made.
     */
    public int makeTurn() {
	roundStart = System.currentTimeMillis();
	System.err.println("Round " + BotParser.round);
	roundMemory.clear();

	final int enemyId = 3 - BotParser.myBotId; // 3-2=1; 3-1=2
	// final int timeBank = BotParser.timeLeft;

	int[] values = new int[COL_ORDER.length];
	Arrays.fill(values, Integer.MIN_VALUE);

	for (int idx = 0; idx < values.length; idx++) {
	    if (field.isValidMove(idx)) {
		final int currentVal = getColumnValue(field.toString(), idx, BotParser.myBotId, enemyId, MAX_BRANCH);
		values[idx] = currentVal;

		final long duration = System.currentTimeMillis() - roundStart;
		System.err.println(idx + " " + currentVal + " at " + duration + "ms");
	    }
	}

	int bestVal = Integer.MIN_VALUE;
	int bestCol = -1;

	// this convoluted piece of code is to make sure we pick the SHORTEST path to the goal
	for (int i = 0; i < COL_ORDER.length; i++) {
	    final int idx = COL_ORDER[i];
	    if (values[idx] > WE_DRAW) { // we can win this game
		if (bestVal <= WE_DRAW) {// first winning solution
		    bestVal = values[idx]; // just assign it
		    bestCol = idx;
		} else { // we are checking between winning solutions
		    // lower winning numbers are lower in the branching tree
		    if (values[idx] < bestVal) {
			bestVal = values[idx];
			bestCol = idx;
		    }
		}
	    } else { // draw or loss
		if (values[idx] > bestVal) { // try and get better
		    bestVal = values[idx];
		    bestCol = idx;
		}
	    }
	}

	final long duration = System.currentTimeMillis() - roundStart;
	System.err.println("Decided on: " + bestCol + " in " + duration + "ms for outcome " + bestVal);

	return bestCol;
    }

    /**
     * Evaluates the value of throwing a coin in a column
     * 
     * @param fieldStr
     *            current state of the field (before throwing the coin)
     * @param column
     *            the column you want to throw in
     * @param player
     *            the player you want to get the value for
     * @param opponent
     *            the opponent of the player
     * @param branch
     *            limiting factor, lower means quicker answer, but less in-depth investigation. Suggested value at least 8
     * @return one of the constants WIN LOSE or DRAW
     */
    private int getColumnValue(final String fieldStr, final int column, final int player, final int opponent, final int branch) {
	// TODO: limit based on time as well?
	if (branch <= 0) { // limit branching for time constraint
	    return WE_DRAW;
	}

	final Field newField = new Field(field.getNrColumns(), field.getNrRows());
	newField.parseFromString(fieldStr);
	newField.addDisc(column, player); // add my coin
	final String newFieldString = newField.toString();
	// System.err.println(newField.toPrettyString()); // uncomment for debug

	// use memorization to quicken the pace
	final Integer roundCacheAnswer = roundMemory.get(newFieldString);
	if (roundCacheAnswer != null) {
	    return (roundCacheAnswer * branch);
	}
	// use memorization to quicken the pace
	final Integer gameCacheAnswer = gameMemory.get(newFieldString);
	if (gameCacheAnswer != null) {
	    return (gameCacheAnswer * branch);
	}

	// easy outcomes
	if (newField.hasFourInARow(player)) {
	    gameMemory.put(newFieldString, WINNING);
	    return (WINNING * branch);
	} else if (newField.hasFourInARow(opponent)) {
	    gameMemory.put(newFieldString, LOSING);
	    return (LOSING * branch);
	} else if (newField.isFull()) {
	    gameMemory.put(newFieldString, WE_DRAW);
	    return WE_DRAW;
	}

	// guess what opponent will do during his move
	int bestEnemyVal = Integer.MIN_VALUE;
	int currentEnemyVal;
	for (int i = 0; i < COL_ORDER.length; i++) {
	    final int idx = COL_ORDER[i];
	    if (newField.isValidMove(idx)) {
		currentEnemyVal = getColumnValue(newFieldString, idx, opponent, player, branch - 1);
		if (currentEnemyVal > bestEnemyVal) { // maximize opponent value
		    bestEnemyVal = currentEnemyVal;
		    if (bestEnemyVal >= WINNING) {
			break; // small optimization
		    }
		}
	    }
	}

	final int ourValue = -bestEnemyVal; // our goal is opposed to that of the opponent
	roundMemory.put(newFieldString, ourValue);
	return ourValue * branch;
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
