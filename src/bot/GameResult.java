package bot;

public class GameResult {
    public static final int WIN = 1;
    public static final int LOSS = -1;
    public static final int DRAW = 0;

    public int player = 0;
    public int result = 0;
    public int depth = 0;

    public GameResult() {}

    public GameResult(final int player, final int result, final int depth) {
	setState(player, result, depth);
    }

    public void setState(final int player, final int result, final int depth) {
	this.player = player;
	this.result = result;
	this.depth = depth;
    }

}
