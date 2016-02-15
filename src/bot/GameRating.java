package bot;

public class GameRating {
    public long wins = 0;
    public long losses = 0;
    public long draws = 0;

    public void reset() {
	wins = 0;
	losses = 0;
	draws = 0;
    }

    public float getWinrate() {
	final long total = wins + draws + losses;
	final float rate = (float) wins / total;
	return rate;
    }

    public float getLossrate() {
	final long total = wins + draws + losses;
	final float rate = (float) losses / total;
	return rate;
    }

    public float getDrawrate() {
	final long total = wins + draws + losses;
	final float rate = (float) draws / total;
	return rate;
    }

    @Override
    public String toString() {
	return String.format("%6d wins, %6d draws, %6d losses \t(%.2f w-r, %.2f d-r, %.2f l-r)", wins, draws, losses, getWinrate(), getDrawrate(), getLossrate());
    }

}
