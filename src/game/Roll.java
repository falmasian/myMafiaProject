package game;

public  class Roll {
 //   private final String playerName;
  private   boolean isAlive=true;
  private  boolean inVoting=false;
  private String rollName;
  private boolean beQuietduringTheDay=false;

 public boolean isBeQuietduringTheDay() {
  return beQuietduringTheDay;
 }

 public void setBeQuietduringTheDay(boolean beQuietduringTheDay) {
  this.beQuietduringTheDay = beQuietduringTheDay;
 }

 public Roll(String rollName) {
  this.rollName = rollName;
 }

 public void setRollName(String rollName) {
  this.rollName = rollName;
 }

 public String getRollName() {
  return rollName;
 }

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
