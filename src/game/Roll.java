package game;

public class Roll {
 //   private final String playerName;
  private   boolean isAlive=true;
  private  boolean inVoting=false;


 public void setAlive(boolean alive) {
  isAlive = alive;

 }

 public boolean isAlive() {
  return isAlive;
 }

 public boolean isInVoting() {
  return inVoting;
 }

 public void setInVoting(boolean inVoting) {
  this.inVoting = inVoting;
 }
}
