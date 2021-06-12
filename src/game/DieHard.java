package game;

public class DieHard extends Citizen{
private  int numberOfQueriesLeft=2;
private int numberOfLivesLeft=2;

    public int getNumberOfQueriesLeft() {
        return numberOfQueriesLeft;
    }
    public void decreaseNumberOfQueriesLeft(){
        numberOfQueriesLeft--;
    }

    public int getNumberOfLivesLeft() {
        return numberOfLivesLeft;
    }
    public void decreaseNumberOfivesLeft(){
        numberOfLivesLeft--;
    }

    public DieHard(String rollName) {
        super(rollName);
    }


}
