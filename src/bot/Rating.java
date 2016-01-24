package bot;

public class Rating {
    public long wins = 0;
    public long losses = 0;
    public long draws = 0;

    public void reset() {
	wins = 0;
	losses = 0;
	draws = 0;
    }

    public long getValue() {
	final float value = (float) (wins * (draws + 1)) / (wins * draws + losses + 1);
	return (long) (100000 * value);
    }

    @Override
    public String toString() {
	final float winRate = (float) wins / (wins + losses + 1);
	final float winDrawRate = (float) (wins * (draws + 1)) / (wins * draws + losses + 1);

	return String.format("%6d wins, %6d draws, %6d losses \t(%.3f winrate, %.3f rate2)", wins, draws, losses, winRate, winDrawRate);
    }

}
