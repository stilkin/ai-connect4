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

    public static final int WINNING = 1000;
    public static final int WE_DRAW = 0;
    public static final int LOSING = -1000;
    private static final int[] COL_ORDER = { 3, 4, 2, 5, 1, 6, 0 };// { 0, 6, 1, 5, 2, 4, 3 };
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
	final int enemyId = 3 - BotParser.mBotId; // 3-2=1; 3-1=2
	final int timeBank = BotParser.timeLeft;

	int bestVal = LOSING;
	int bestCol = -1;
	int currentVal;
	for (int i = 0; i < COL_ORDER.length; i++) {
	    if (field.isValidMove(COL_ORDER[i])) {
		currentVal = getColumnValue(field.toString(), COL_ORDER[i], BotParser.mBotId, enemyId, 9);
		System.err.println(COL_ORDER[i] + " " + currentVal);
		if (currentVal >= bestVal) { // minimize opponent value
		    bestVal = currentVal;
		    bestCol = COL_ORDER[i];
		    if (bestVal == WINNING) {
			break;
		    } else if (bestVal == WE_DRAW) {
			final long duration = System.currentTimeMillis() - roundStart;
			if (duration > timeBank / 2) {
			    System.err.println("This is taking too long...");
			    break;
			}
		    }
		}

	    }
	    final long duration = System.currentTimeMillis() - roundStart;
	    // System.err.println("Currently " + duration + " ms");
	    if (bestVal == WE_DRAW) {
		if (duration > timeBank / 2) {
		    System.err.println("This is taking too long...");
		    break;
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
	// TODO: limit based on time as well
	if (branch <= 0) { // limit branching for time constraint
	    return LOSING; // assume worst case?
	}

	final Field newField = new Field(field.getNrColumns(), field.getNrRows());
	newField.parseFromString(fieldStr);
	newField.addDisc(column, player); // add my coin
	final String newFieldString = newField.toString();
	// System.err.println(newField.toPrettyString()); // uncomment for debug

	// use memorization to quicken the pace
	final Integer gameCacheAnswer = gameMemory.get(newFieldString);
	if (gameCacheAnswer != null) {
	    return gameCacheAnswer;
	}

	// easy outcomes
	if (newField.hasFourInARow(player)) {
	    // System.err.println(newField.toPrettyString());
	    gameMemory.put(newFieldString, WINNING);
	    return WINNING;
	} else if (newField.hasFourInARow(opponent)) {
	    // System.err.println(newField.toPrettyString());
	    gameMemory.put(newFieldString, LOSING);
	    return LOSING;
	} else if (newField.isFull()) {
	    // System.err.println(newField.toPrettyString());
	    gameMemory.put(newFieldString, WE_DRAW);
	    return WE_DRAW;
	}

	// guess what opponent will do during his move
	int bestEnemyVal = LOSING;
	int currentEnemyVal;
	for (int i = 0; i < COL_ORDER.length; i++) {
	    if (newField.isValidMove(COL_ORDER[i])) {
		currentEnemyVal = getColumnValue(newFieldString, COL_ORDER[i], opponent, player, branch - 1);
		if (currentEnemyVal > bestEnemyVal) { // maximize opponent value
		    bestEnemyVal = currentEnemyVal;
		    if (bestEnemyVal == WINNING) {
			break;
		    }
		}
	    }
	}

	final int ourValue = -bestEnemyVal; // our goal is opposed to that of the opponent
	return ourValue;
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
