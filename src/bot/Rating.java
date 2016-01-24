package bot;

public class Rating {
    public static final int ALWAYS_WINNING = 9999;
    public long wins = 0;
    public long losses = 0;
    public long draws = 0;

    public void reset() {
	wins = 0;
	losses = 0;
	draws = 0;
    }

    public int getValue() {
	if (losses == 0) {
	    // "We simply cannot lose" ;-)
	    return ALWAYS_WINNING;
	}

	final float winRate = (float) wins / (wins + losses + 1);
	final float drawRate = (float) draws / (draws + losses + 1);

	float value = 3 * winRate + 2 * drawRate;
	return (int) (1000 * value);
    }

    @Override
    public String toString() {
	final float winRate = (float) wins / (wins + losses + 1);
	final float drawRate = (float) draws / (draws + losses + 1);

	return String.format("%6d wins, %6d draws, %6d losses \t(%.3f rate1, %.3f rate2)", wins, draws, losses, winRate, drawRate);
    }

}
