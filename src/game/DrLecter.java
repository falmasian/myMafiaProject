package game;

/**
 * The type Dr lecter.
 */
public class DrLecter extends Mafia {
    private boolean saveHimself = false;

    /**
     * Is save himself boolean.
     *
     * @return the boolean
     */
    public boolean isSaveHimself() {
        return saveHimself;
    }

    /**
     * Sets save himself.
     *
     * @param saveHimself the save himself
     */
    public void setSaveHimself(boolean saveHimself) {
        this.saveHimself = saveHimself;
    }

    /**
     * Instantiates a new Dr lecter.
     *
     * @param rollName the roll name
     */
    public DrLecter(String rollName) {
        super(rollName);
    }

}
