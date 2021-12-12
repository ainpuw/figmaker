package com.ainpuw.figmaker.scenarios;

import com.ainpuw.figmaker.Config;
import com.badlogic.gdx.utils.Queue;

public abstract class Scenario {
    public Config config;
    public Queue<Event> eventQueue;
    public Event currentEvent = null;

    public int spawnAnimation; // Not int.
    public int wormCoords;  // Not int.

    public Scenario(Config config) {
        this.config = config;
        this.eventQueue = new Queue<>();
    }

    public abstract Scenario NextScenario();

}
