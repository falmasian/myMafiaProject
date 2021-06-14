package game;

/**
 * The type Die hard.
 */
public class DieHard extends Citizen {
    private int numberOfQueriesLeft = 2;
    private int numberOfLivesLeft = 2;

    /**
     * Gets number of queries left.
     *
     * @return the number of queries left
     */
    public int getNumberOfQueriesLeft() {
        return numberOfQueriesLeft;
    }

    /**
     * Decrease number of queries left.
     */
    public void decreaseNumberOfQueriesLeft() {
        numberOfQueriesLeft--;
    }

    /**
     * Gets number of lives left.
     *
     * @return the number of lives left
     */
    public int getNumberOfLivesLeft() {
        return numberOfLivesLeft;
    }

    /**
     * Decrease number ofives left.
     */
    public void decreaseNumberOfivesLeft() {
        numberOfLivesLeft--;
    }

    /**
     * Instantiates a new Die hard.
     *
     * @param rollName the roll name
     */
    public DieHard(String rollName) {
        super(rollName);
    }


}
