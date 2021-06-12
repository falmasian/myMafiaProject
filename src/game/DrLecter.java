package game;

public class DrLecter extends Mafia{
private boolean saveHimself =false;

    public boolean isSaveHimself() {
        return saveHimself;
    }

    public void setSaveHimself(boolean saveHimself) {
        this.saveHimself = saveHimself;
    }

    public DrLecter(String  rollName) {
        super(rollName);
    }

}
