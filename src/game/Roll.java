package game;

/**
 * The type Roll.
 */
public class Roll {
    //   private final String playerName;
    private boolean isAlive = true;
    private boolean inVoting = false;
    private String rollName;
    private boolean beQuietDuringTheDay = false;

    /**
     * Is  quiet during the day boolean.
     *
     * @return the boolean
     */
    public boolean isBeQuietDuringTheDay() {
        return beQuietDuringTheDay;
    }

    /**
     * Sets be quietduring the day.
     *
     * @param beQuietduringTheDay the be quietduring the day
     */
    public void setBeQuietduringTheDay(boolean beQuietduringTheDay) {
        this.beQuietDuringTheDay = beQuietduringTheDay;
    }

    /**
     * Instantiates a new Roll.
     *
     * @param rollName the roll name
     */
    public Roll(String rollName) {
        this.rollName = rollName;
    }

    /**
     * Sets roll name.
     *
     * @param rollName the roll name
     */
    public void setRollName(String rollName) {
        this.rollName = rollName;
    }

    /**
     * Gets roll name.
     *
     * @return the roll name
     */
    public String getRollName() {
        return rollName;
    }

    /**
     * Sets alive.
     *
     * @param alive the alive
     */
    public void setAlive(boolean alive) {
        isAlive = alive;

    }

    /**
     * Is alive boolean.
     *
     * @return the boolean
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Is in voting boolean.
     *
     * @return the boolean
     */
    public boolean isInVoting() {
        return inVoting;
    }

    /**
     * Sets in voting.
     *
     * @param inVoting the in voting
     */
    public void setInVoting(boolean inVoting) {
        this.inVoting = inVoting;
    }
}
