package com.example.nemol.googlephotokiller;

/**
 * Created by nemol on 03.03.2018.
 */

public class ServerDoneEvent {

    private final boolean done;

    public ServerDoneEvent(boolean done) {
        this.done = done;
    }

    public boolean getState() {
        return done;
    }
}
