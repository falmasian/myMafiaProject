package game;

/**
 * The type Doctor.
 */
public class Doctor extends Citizen {


    /**
     * Instantiates a new Doctor.
     *
     * @param rollName the roll name
     */
    public Doctor(String rollName) {
        super(rollName);
    }

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
}
