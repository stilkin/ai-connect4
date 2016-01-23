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
	final long val = (4 * wins) - (4 * losses) /* + draws */;
	return val;
    }

    @Override
    public String toString() {
	return "Rating [wins=" + wins + ", losses=" + losses + ", draws=" + draws + "]";
    }

}
