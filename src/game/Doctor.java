package game;

public class Doctor extends Citizen{


    public Doctor(String rollName) {
        super(rollName);
    }
    private boolean saveHimself =false;

    public boolean isSaveHimself() {
        return saveHimself;
    }

    public void setSaveHimself(boolean saveHimself) {
        this.saveHimself = saveHimself;
    }
}
