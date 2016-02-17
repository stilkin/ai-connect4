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
	final float rate = (float) wins / getTotal();
	return rate;
    }

    public float getLossrate() {
	final float rate = (float) losses / getTotal();
	return rate;
    }

    public float getDrawrate() {
	final float rate = (float) draws / getTotal();
	return rate;
    }

    private long getTotal() {
	return (wins + draws + losses);
    }

    @Override
    public String toString() {
	return String.format("%6d wins, %6d draws, %6d losses \t(%.2f w-r, %.2f d-r, %.2f l-r)", wins, draws, losses, getWinrate(), getDrawrate(), getLossrate());
    }

}
