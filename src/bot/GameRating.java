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

	final long total = wins + draws + losses;
	// final float value = (float) ((wins + 1) * (draws + 1)) / (wins * draws + losses + 1);
	final float value = ((10 * wins + 1) * (draws + 1) / (float) total);
	return (long) (10 * value);
    }

    @Override
    public String toString() {
	final long total = wins + draws + losses;
	final float winRate = (float) wins / total;
	final float lossRate = (float) losses / total;
	final float winDrawRate = ((10 * wins + 1) * (draws + 1) / (float) total);

	return String.format("%6d wins, %6d draws, %6d losses \t(%.2f w-r, %.2f c-r, %.2f l-r)", wins, draws, losses, winRate, winDrawRate, lossRate);
    }

}
