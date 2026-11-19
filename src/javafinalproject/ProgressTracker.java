package javafinalproject;

public class ProgressTracker {
    private int currentLevel;
    private int unlockedLevel;

    public ProgressTracker() {
        this.currentLevel = 1;
        this.unlockedLevel = 1;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getUnlockedLevel() {
        return unlockedLevel;
    }

    public void completeLevel() {
        if (currentLevel >= unlockedLevel) {
            unlockedLevel = currentLevel + 1;
        }
    }

    public void resetProgress() {
        this.currentLevel = 1;
        this.unlockedLevel = 1;
    }
}