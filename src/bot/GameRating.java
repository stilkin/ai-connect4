package bot;

public class GameRating {
    private static final long ALWAYS_WINNING = Integer.MAX_VALUE;
    public long wins = 0;
    public long losses = 0;
    public long draws = 0;

    public void reset() {
	wins = 0;
	losses = 0;
	draws = 0;
    }

    public long getValue() {
	if (losses == 0) {
	    // we cannot lose here
	    return ALWAYS_WINNING;
	}

	if (wins == 0) {
	    wins = draws;
	}

	// final float value = (float) ((wins + 1) * (draws + 1)) / (wins * draws + losses + 1);
	final float value = (float) losses / (wins * draws + 1);
	return (long) (-100000 * value);
    }

    @Override
    public String toString() {
	final float winRate = (float) wins / (wins + losses + 1);
	final float lossRate = (float) losses / (wins * draws + 1);
	final float winDrawRate = (float) ((wins + 1) * (draws + 1)) / (wins * draws + losses + 1);

	return String.format("%6d wins, %6d draws, %6d losses \t(%.3f winrate, %.3f rate2, %.3f lossrate)", wins, draws, losses, winRate, winDrawRate, lossRate);
    }

}
