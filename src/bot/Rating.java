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

    public int getValue() {
	final float winRate = (float) wins / (wins + losses + 1);
	final float drawRate = (float) (wins + draws) / (wins + draws + losses);

	float value = 5 * winRate;
	value += drawRate;

	return (int) (1000 * value);
    }

    @Override
    public String toString() {
	final float winRate = (float) wins / (wins + losses + 1);
	final float drawRate = (float) (wins + draws) / (wins + draws + losses);

	return String.format("%6d wins, %6d draws, %6d losses \t(%.3f rate1, %.3f rate2)", wins, draws, losses, winRate, drawRate);
    }

}
