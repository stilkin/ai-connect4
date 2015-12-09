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

/**
 * BotStarter class
 * 
 * Magic happens here. You should edit this file, or more specifically the makeTurn() method to make your bot do more than random moves.
 * 
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 * @author Servaas Tilkin ( edits and patches)
 */

public class BotStarter {
    public static final int WINNING = 100;
    public static final int WE_DRAW = 0;
    public static final int LOSING = -100;
    public static final int COLS = 7;
    public static BotParser parser;
    private static final int[] COL_ORDER = { 3, 4, 2, 5, 1, 6, 0 }; // { 0, 6, 1, 5, 2, 4, 3 };
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
	System.err.println(field.toPrettyString());

	final int enemyId = 3 - BotParser.myBotId; // 3-2=1; 3-1=2
	// final int timeBank = BotParser.timeLeft;

	// check for winning moves
	for (int idx = 0; idx < COLS; idx++) {
	    if (field.isValidMove(idx)) {
		field.addDisc(idx, BotParser.myBotId);
		if (field.hasFourInARow(BotParser.myBotId)) {
		    System.err.println("Going for quick win in col " + idx);
		    return idx;
		}
		field.removeDisc(idx);
	    }
	}

	// check to block enemy winning moves
	for (int idx = 0; idx < COLS; idx++) {
	    if (field.isValidMove(idx)) {
		field.addDisc(idx, enemyId);
		if (field.hasFourInARow(enemyId)) {
		    System.err.println("Preventing enemy win in col " + idx);
		    return idx;
		}
		field.removeDisc(idx);
	    }
	}

	final int[] values = new int[COL_ORDER.length];
	Arrays.fill(values, LOSING);

	// fill up all columns to see if there is a win / loss coming
	int player, depth;
	for (int idx = 0; idx < COLS; idx++) {
	    player = BotParser.myBotId;
	    depth = 0;
	    field.resetToInitialRoundState();
	    while (field.isValidMove(idx)) {
		depth++;
		field.addDisc(idx, player);

		if (field.hasFourInARow(player)) {
		    if (player == enemyId) { // future enemy win
			values[idx] = LOSING + depth;
		    } else {// future win for us
			values[idx] = WINNING - depth;
		    }
		    break; // game ends here
		} else {
		    values[idx] = WE_DRAW + depth; // no imminent loss here
		}

		player = 3 - player;
	    }
	    final long duration = System.currentTimeMillis() - roundStart;
	    System.err.println(idx + " " + values[idx] + " at " + duration + "ms");
	}

	int bestVal = Integer.MIN_VALUE;
	int bestCol = -1;
	// find out our best value
	for (int i = 0; i < COL_ORDER.length; i++) {
	    final int idx = COL_ORDER[i];
	    if (field.isValidMove(idx)) {
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
     * MAIN METHOD
     */
    public static void main(String[] args) {
	parser = new BotParser(new BotStarter());
	parser.run();
    }

}
