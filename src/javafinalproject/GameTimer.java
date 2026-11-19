//Caleb

package javafinalproject;

import javax.swing.Timer;

//Tracks elapsed game time using a Swing timer 

public class GameTimer {
	//Total time elapsed while the timer is active
    private int elapsedSeconds = 0;
    private boolean running = false;
    //Swing timer that fires every 1000 milliseconds (or 1 second)
    private final Timer swingTimer;

    
    //Creates 1-Second game timer
    public GameTimer(Runnable onTick) {
        this.swingTimer = new Timer(1000, e -> {
            if (running) {
                elapsedSeconds++;
                if (onTick != null) {
                    onTick.run();
                }
            }
        });
    }

    //Starts or resumes the timer
    public void start() {
        running = true;
        swingTimer.start();
    }

    //Pauses the timer without resetting the time
    public void stop() {
        running = false;
        swingTimer.stop();
    }

    //Stops the timer and resets the time back to zero
    public void reset() {
        stop();
        elapsedSeconds = 0;
    }
    //Formats the time as minutes:seconds 
    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public String getFormattedTime() {
        return String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60);
    }
}